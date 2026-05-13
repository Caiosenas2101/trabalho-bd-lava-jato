const formCliente = document.getElementById('formCliente');
const formServico = document.getElementById('formServico');
const listaClientes = document.getElementById('listaClientes');
const listaServicos = document.getElementById('listaServicos');
const botaoCliente = document.getElementById('botaoCliente');
const botaoServico = document.getElementById('botaoServico');
const cancelarCliente = document.getElementById('cancelarCliente');
const cancelarServico = document.getElementById('cancelarServico');

function mostrarErro(contexto, erro) {
  console.error(`Erro em ${contexto}:`, erro);
  alert(`Nao foi possivel concluir a operacao de ${contexto}.`);
}

async function enviarJson(url, method, dados) {
  const opcoes = { method };
  if (dados !== undefined) {
    opcoes.headers = { 'Content-Type': 'application/json' };
    opcoes.body = JSON.stringify(dados);
  }

  const resposta = await fetch(url, opcoes);

  if (!resposta.ok) {
    throw new Error(await resposta.text());
  }

  if (resposta.status === 204) {
    return null;
  }

  return resposta.json();
}

function criarBotaoAcao(texto, classe, aoClicar) {
  const botao = document.createElement('button');
  botao.type = 'button';
  botao.className = classe;
  botao.textContent = texto;
  botao.addEventListener('click', aoClicar);
  return botao;
}

function limparFormularioCliente() {
  formCliente.reset();
  document.getElementById('idCliente').value = '';
  botaoCliente.textContent = 'Cadastrar Cliente';
  cancelarCliente.classList.add('oculto');
}

function limparFormularioServico() {
  formServico.reset();
  document.getElementById('idServico').value = '';
  botaoServico.textContent = 'Cadastrar Serviço';
  cancelarServico.classList.add('oculto');
}

function preencherFormularioCliente(cliente) {
  document.getElementById('idCliente').value = cliente.idCliente;
  document.getElementById('nome').value = cliente.nome || '';
  document.getElementById('cpf').value = cliente.cpf || '';
  document.getElementById('email').value = cliente.email || '';
  document.getElementById('enderecoRua').value = cliente.enderecoRua || '';
  document.getElementById('enderecoBairro').value = cliente.enderecoBairro || '';
  document.getElementById('enderecoCidade').value = cliente.enderecoCidade || '';
  botaoCliente.textContent = 'Atualizar Cliente';
  cancelarCliente.classList.remove('oculto');
}

function preencherFormularioServico(servico) {
  document.getElementById('idServico').value = servico.idServico;
  document.getElementById('nomeServico').value = servico.nomeServico || '';
  document.getElementById('preco').value = servico.preco || '';
  document.getElementById('tempoMin').value = servico.tempoMin || '';
  document.getElementById('descricao').value = servico.descricao || '';
  botaoServico.textContent = 'Atualizar Serviço';
  cancelarServico.classList.remove('oculto');
}

async function carregarClientes() {
  try {
    const resposta = await fetch('/clientes');
    const clientes = await resposta.json();
    listaClientes.innerHTML = '';

    clientes.forEach((cliente) => {
      const li = document.createElement('li');
      const texto = document.createElement('span');
      const acoes = document.createElement('div');

      li.className = 'item-lista';
      acoes.className = 'acoes-item';
      texto.textContent = `${cliente.idCliente} - ${cliente.nome} - ${cliente.cpf}`;

      acoes.appendChild(criarBotaoAcao('Editar', 'secundario', () => preencherFormularioCliente(cliente)));
      acoes.appendChild(criarBotaoAcao('Excluir', 'perigo', () => excluirCliente(cliente.idCliente)));

      li.appendChild(texto);
      li.appendChild(acoes);
      listaClientes.appendChild(li);
    });
  } catch (erro) {
    mostrarErro('listar clientes', erro);
  }
}

async function carregarServicos() {
  try {
    const resposta = await fetch('/servicos');
    const servicos = await resposta.json();
    listaServicos.innerHTML = '';

    servicos.forEach((servico) => {
      const li = document.createElement('li');
      const texto = document.createElement('span');
      const acoes = document.createElement('div');

      li.className = 'item-lista';
      acoes.className = 'acoes-item';
      texto.textContent = `${servico.idServico} - ${servico.nomeServico} - R$ ${servico.preco}`;

      acoes.appendChild(criarBotaoAcao('Editar', 'secundario', () => preencherFormularioServico(servico)));
      acoes.appendChild(criarBotaoAcao('Excluir', 'perigo', () => excluirServico(servico.idServico)));

      li.appendChild(texto);
      li.appendChild(acoes);
      listaServicos.appendChild(li);
    });
  } catch (erro) {
    mostrarErro('listar serviços', erro);
  }
}

async function excluirCliente(idCliente) {
  if (!confirm('Deseja excluir este cliente?')) {
    return;
  }

  try {
    await enviarJson(`/clientes/${idCliente}`, 'DELETE');
    limparFormularioCliente();
    await carregarClientes();
  } catch (erro) {
    mostrarErro('excluir cliente', erro);
  }
}

async function excluirServico(idServico) {
  if (!confirm('Deseja excluir este serviço?')) {
    return;
  }

  try {
    await enviarJson(`/servicos/${idServico}`, 'DELETE');
    limparFormularioServico();
    await carregarServicos();
  } catch (erro) {
    mostrarErro('excluir serviço', erro);
  }
}

formCliente.addEventListener('submit', async (evento) => {
  evento.preventDefault();

  const idCliente = document.getElementById('idCliente').value;
  const cliente = {
    nome: document.getElementById('nome').value,
    cpf: document.getElementById('cpf').value,
    email: document.getElementById('email').value,
    enderecoRua: document.getElementById('enderecoRua').value,
    enderecoBairro: document.getElementById('enderecoBairro').value,
    enderecoCidade: document.getElementById('enderecoCidade').value
  };

  try {
    const url = idCliente ? `/clientes/${idCliente}` : '/clientes';
    const metodo = idCliente ? 'PUT' : 'POST';
    await enviarJson(url, metodo, cliente);
    limparFormularioCliente();
    await carregarClientes();
  } catch (erro) {
    mostrarErro(idCliente ? 'atualizar cliente' : 'cadastrar cliente', erro);
  }
});

formServico.addEventListener('submit', async (evento) => {
  evento.preventDefault();

  const idServico = document.getElementById('idServico').value;
  const servico = {
    nomeServico: document.getElementById('nomeServico').value,
    preco: parseFloat(document.getElementById('preco').value),
    tempoMin: parseInt(document.getElementById('tempoMin').value, 10),
    descricao: document.getElementById('descricao').value
  };

  try {
    const url = idServico ? `/servicos/${idServico}` : '/servicos';
    const metodo = idServico ? 'PUT' : 'POST';
    await enviarJson(url, metodo, servico);
    limparFormularioServico();
    await carregarServicos();
  } catch (erro) {
    mostrarErro(idServico ? 'atualizar serviço' : 'cadastrar serviço', erro);
  }
});

cancelarCliente.addEventListener('click', limparFormularioCliente);
cancelarServico.addEventListener('click', limparFormularioServico);

limparFormularioCliente();
limparFormularioServico();
carregarClientes();
carregarServicos();