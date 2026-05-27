const $ = (id) => document.getElementById(id);

const estado = {
  clientes: [],
  servicos: [],
  veiculosCrud: [],
  funcionarios: [],
  atendimentos: [],
  avaliacoes: [],
  veiculos: []
};

const graficosDashboard = {
  faturamento: null,
  status: null,
  tendencia: null,
  notas: null,
  topClientes: null
};

const paletaCores = [
  '#2563eb', '#15803d', '#dc2626', '#f59e0b', '#9333ea',
  '#0891b2', '#db2777', '#65a30d', '#ea580c', '#475569',
  '#7c3aed', '#0ea5e9', '#16a34a', '#e11d48', '#facc15'
];

function cor(indice) {
  return paletaCores[indice % paletaCores.length];
}

function coresParaItens(qtd) {
  return Array.from({ length: qtd }, (_, i) => cor(i));
}

function destruirGrafico(chave) {
  if (graficosDashboard[chave]) {
    graficosDashboard[chave].destroy();
    graficosDashboard[chave] = null;
  }
}

function formatarMoeda(valor) {
  const numero = Number(valor || 0);
  return numero.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function formatarNumero(valor) {
  if (valor === null || valor === undefined || valor === '') return '0';
  return Number(valor).toLocaleString('pt-BR', { maximumFractionDigits: 2 });
}

function avisar(texto, erro = false) {
  const mensagem = $('mensagem');
  mensagem.textContent = texto;
  mensagem.style.background = erro ? '#fef2f2' : '#ecfdf5';
  mensagem.style.color = erro ? '#991b1b' : '#166534';
  mensagem.style.borderColor = erro ? '#fecaca' : '#bbf7d0';
  mensagem.classList.remove('oculto');
  setTimeout(() => mensagem.classList.add('oculto'), 3600);
}

function tratarErro(contexto, erro) {
  console.error(`Erro em ${contexto}:`, erro);
  let detalhe = erro && erro.message ? erro.message : '';
  if (detalhe.includes('Duplicate entry')) {
    detalhe = 'Ja existe um registro com algum campo unico repetido, como CPF ou e-mail.';
  } else if (detalhe.includes('foreign key constraint')) {
    detalhe = 'Algum ID informado nao existe ou esta ligado a outro cadastro.';
  } else if (detalhe.length > 180) {
    detalhe = detalhe.slice(0, 180) + '...';
  }
  avisar(`Nao foi possivel concluir: ${contexto}. ${detalhe}`, true);
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
  return resposta.status === 204 ? null : resposta.json();
}

async function buscarJson(url) {
  const resposta = await fetch(url);
  if (!resposta.ok) {
    throw new Error(await resposta.text());
  }
  return resposta.json();
}

function valorOuVazio(valor) {
  return valor === null || valor === undefined ? '' : valor;
}

function numeroOuNull(valor) {
  return valor === '' ? null : Number(valor);
}

function preencherSelect(selectId, itens, valorItem, textoItem, textoInicial = 'Selecione') {
  const select = $(selectId);
  const valorAtual = select.value;
  select.innerHTML = '';

  const inicial = document.createElement('option');
  inicial.value = '';
  inicial.textContent = textoInicial;
  select.appendChild(inicial);

  itens.forEach((item) => {
    const opcao = document.createElement('option');
    opcao.value = valorItem(item);
    opcao.textContent = textoItem(item);
    select.appendChild(opcao);
  });

  if (valorAtual) {
    select.value = valorAtual;
  }
}

function criarBotao(texto, classe, acao) {
  const botao = document.createElement('button');
  botao.type = 'button';
  botao.className = classe;
  botao.textContent = texto;
  botao.addEventListener('click', acao);
  return botao;
}

function renderizarLista(idElemento, itens, formatar, editar, excluir) {
  const lista = $(idElemento);
  lista.innerHTML = '';

  itens.slice(0, 60).forEach((item) => {
    const linha = document.createElement('div');
    const texto = document.createElement('div');
    const acoes = document.createElement('div');
    linha.className = 'item-lista';
    acoes.className = 'acoes-item';
    texto.innerHTML = formatar(item);
    acoes.appendChild(criarBotao('Editar', 'secundario', () => editar(item)));
    acoes.appendChild(criarBotao('Excluir', 'perigo', () => excluir(item)));
    linha.appendChild(texto);
    linha.appendChild(acoes);
    lista.appendChild(linha);
  });
}

function renderizarTabela(containerId, dados) {
  const container = $(containerId);
  container.innerHTML = '';

  if (!Array.isArray(dados)) {
    container.textContent = dados && dados.message ? dados.message : 'Resposta inesperada do servidor.';
    return;
  }

  if (!dados || dados.length === 0) {
    container.textContent = 'Nenhum registro encontrado.';
    return;
  }

  const colunas = Object.keys(dados[0]);
  const tabela = document.createElement('table');
  const thead = document.createElement('thead');
  const tbody = document.createElement('tbody');
  const linhaCabecalho = document.createElement('tr');

  colunas.forEach((coluna) => {
    const th = document.createElement('th');
    th.textContent = coluna;
    linhaCabecalho.appendChild(th);
  });
  thead.appendChild(linhaCabecalho);

  dados.forEach((item) => {
    const tr = document.createElement('tr');
    colunas.forEach((coluna) => {
      const td = document.createElement('td');
      td.textContent = valorOuVazio(item[coluna]);
      tr.appendChild(td);
    });
    tbody.appendChild(tr);
  });

  tabela.appendChild(thead);
  tabela.appendChild(tbody);
  container.appendChild(tabela);
}

function mostrarElemento(idElemento) {
  $(idElemento).classList.remove('oculto');
}

function alternarElemento(idElemento) {
  $(idElemento).classList.toggle('oculto');
}

function limparCliente() {
  $('formCliente').reset();
  $('idCliente').value = '';
  $('botaoCliente').textContent = 'Cadastrar';
  $('cancelarCliente').classList.add('oculto');
}

function preencherCliente(cliente) {
  mostrarElemento('formCliente');
  $('idCliente').value = cliente.idCliente;
  $('nome').value = valorOuVazio(cliente.nome);
  $('cpf').value = valorOuVazio(cliente.cpf);
  $('email').value = valorOuVazio(cliente.email);
  $('enderecoRua').value = valorOuVazio(cliente.enderecoRua);
  $('enderecoBairro').value = valorOuVazio(cliente.enderecoBairro);
  $('enderecoCidade').value = valorOuVazio(cliente.enderecoCidade);
  $('botaoCliente').textContent = 'Atualizar';
  $('cancelarCliente').classList.remove('oculto');
}

function limparServico() {
  $('formServico').reset();
  $('idServico').value = '';
  $('botaoServico').textContent = 'Cadastrar';
  $('cancelarServico').classList.add('oculto');
}

function preencherServico(servico) {
  mostrarElemento('formServico');
  $('idServico').value = servico.idServico;
  $('nomeServico').value = valorOuVazio(servico.nomeServico);
  $('preco').value = valorOuVazio(servico.preco);
  $('tempoMin').value = valorOuVazio(servico.tempoMin);
  $('descricao').value = valorOuVazio(servico.descricao);
  $('botaoServico').textContent = 'Atualizar';
  $('cancelarServico').classList.remove('oculto');
}

function limparVeiculo() {
  $('formVeiculo').reset();
  $('placaVeiculoOriginal').value = '';
  $('idClienteVeiculoOriginal').value = '';
  $('placaVeiculoCadastro').disabled = false;
  $('clienteVeiculoCadastro').disabled = false;
  $('botaoVeiculo').textContent = 'Cadastrar';
  $('cancelarVeiculo').classList.add('oculto');
}

function preencherVeiculo(veiculo) {
  mostrarElemento('formVeiculo');
  $('placaVeiculoOriginal').value = veiculo.placa;
  $('idClienteVeiculoOriginal').value = veiculo.idCliente;
  $('placaVeiculoCadastro').value = valorOuVazio(veiculo.placa);
  $('clienteVeiculoCadastro').value = valorOuVazio(veiculo.idCliente);
  $('modeloVeiculo').value = valorOuVazio(veiculo.modelo);
  $('corVeiculo').value = valorOuVazio(veiculo.cor);
  $('anoVeiculo').value = valorOuVazio(veiculo.ano);
  $('placaVeiculoCadastro').disabled = true;
  $('clienteVeiculoCadastro').disabled = true;
  $('botaoVeiculo').textContent = 'Atualizar';
  $('cancelarVeiculo').classList.remove('oculto');
}

function limparFuncionario() {
  $('formFuncionario').reset();
  $('idFuncionario').value = '';
  $('botaoFuncionario').textContent = 'Cadastrar';
  $('cancelarFuncionario').classList.add('oculto');
}

function preencherFuncionario(funcionario) {
  mostrarElemento('formFuncionario');
  $('idFuncionario').value = funcionario.idFuncionario;
  $('nomeFuncionario').value = valorOuVazio(funcionario.nome);
  $('cargoFuncionario').value = valorOuVazio(funcionario.cargo);
  $('idSupervisor').value = valorOuVazio(funcionario.idSupervisor);
  $('botaoFuncionario').textContent = 'Atualizar';
  $('cancelarFuncionario').classList.remove('oculto');
}

function limparAtendimento() {
  $('formAtendimento').reset();
  $('idAtendimento').value = '';
  $('statusAtendimento').value = 'agendado';
  $('botaoAtendimento').textContent = 'Cadastrar';
  $('cancelarAtendimento').classList.add('oculto');
}

function limparAvaliacao() {
  $('formAvaliacao').reset();
  $('idAvaliacao').value = '';
  $('idClienteAvaliacao').value = '';
  $('clienteAvaliacaoNome').value = '';
  $('idAtendimentoAvaliacao').disabled = false;
  $('botaoAvaliacao').textContent = 'Cadastrar';
  $('cancelarAvaliacao').classList.add('oculto');
  preencherSelectAtendimentosAvaliacao();
}

function atendimentoPorId(idAtendimento) {
  return estado.atendimentos.find((a) => Number(a.idAtendimento) === Number(idAtendimento));
}

function preencherSelectAtendimentosAvaliacao() {
  const idAtual = $('idAtendimentoAvaliacao') ? $('idAtendimentoAvaliacao').value : '';
  const avaliados = new Set(
    estado.avaliacoes
      .filter((a) => Number(a.idAtendimento) !== Number(idAtual))
      .map((a) => Number(a.idAtendimento))
  );
  const disponiveis = estado.atendimentos.filter((a) => !avaliados.has(Number(a.idAtendimento)));

  preencherSelect(
    'idAtendimentoAvaliacao',
    disponiveis,
    (a) => a.idAtendimento,
    (a) => `${a.idAtendimento} - ${a.placaVeiculo} (${a.data})`,
    'Atendimento sem avaliacao'
  );

  if (idAtual && disponiveis.some((a) => Number(a.idAtendimento) === Number(idAtual))) {
    $('idAtendimentoAvaliacao').value = idAtual;
  }

  atualizarClienteDaAvaliacao();
}

function clientePorId(idCliente) {
  return estado.clientes.find((c) => Number(c.idCliente) === Number(idCliente));
}

function atualizarClienteDaAvaliacao() {
  const atendimento = atendimentoPorId($('idAtendimentoAvaliacao').value);
  if (!atendimento) {
    $('idClienteAvaliacao').value = '';
    $('clienteAvaliacaoNome').value = '';
    return;
  }

  const cliente = clientePorId(atendimento.idClienteVeiculo);
  $('idClienteAvaliacao').value = atendimento.idClienteVeiculo;
  $('clienteAvaliacaoNome').value = cliente ? cliente.nome : `Cliente ${atendimento.idClienteVeiculo}`;
}

function preencherAvaliacao(avaliacao) {
  mostrarElemento('formAvaliacao');
  $('idAvaliacao').value = avaliacao.idAvaliacao;
  $('idAtendimentoAvaliacao').value = valorOuVazio(avaliacao.idAtendimento);
  $('idAtendimentoAvaliacao').disabled = true;
  atualizarClienteDaAvaliacao();
  $('idClienteAvaliacao').value = valorOuVazio(avaliacao.idCliente);
  const cliente = clientePorId(avaliacao.idCliente);
  $('clienteAvaliacaoNome').value = cliente ? cliente.nome : `Cliente ${avaliacao.idCliente}`;
  $('notaAvaliacao').value = valorOuVazio(avaliacao.nota);
  $('comentariosAvaliacao').value = valorOuVazio(avaliacao.comentarios);
  $('dataAvaliacao').value = valorOuVazio(avaliacao.data);
  $('botaoAvaliacao').textContent = 'Atualizar';
  $('cancelarAvaliacao').classList.remove('oculto');
}

function preencherAtendimento(atendimento) {
  mostrarElemento('formAtendimento');
  $('idAtendimento').value = atendimento.idAtendimento;
  $('veiculoAtendimento').value = `${atendimento.placaVeiculo}|${atendimento.idClienteVeiculo}`;
  $('idServicoAtendimento').value = valorOuVazio(atendimento.idServico);
  $('idFuncionarioAtendimento').value = valorOuVazio(atendimento.idFuncionario);
  $('dataAtendimento').value = valorOuVazio(atendimento.data);
  $('horaAtendimento').value = valorOuVazio(String(atendimento.hora).slice(0, 5));
  $('statusAtendimento').value = atendimento.status || 'agendado';
  $('botaoAtendimento').textContent = 'Atualizar';
  $('cancelarAtendimento').classList.remove('oculto');
}

async function carregarClientes() {
  estado.clientes = await buscarJson('/clientes');
  const clientesOrdenados = [...estado.clientes].sort((a, b) => b.idCliente - a.idCliente);
  preencherSelect(
    'clienteVeiculoCadastro',
    estado.clientes,
    (c) => c.idCliente,
    (c) => `${c.nome} - CPF ${c.cpf}`,
    'Dono do veiculo'
  );
  renderizarLista(
    'listaClientes',
    clientesOrdenados,
    (c) => `<strong>${c.idCliente} - ${c.nome}</strong><small>CPF ${c.cpf} | ${valorOuVazio(c.email)}</small>`,
    preencherCliente,
    (c) => excluirRegistro(`/clientes/${c.idCliente}`, carregarClientes, limparCliente)
  );
}

async function carregarServicos() {
  estado.servicos = await buscarJson('/servicos');
  const servicosOrdenados = [...estado.servicos].sort((a, b) => b.idServico - a.idServico);
  preencherSelect(
    'idServicoAtendimento',
    estado.servicos,
    (s) => s.idServico,
    (s) => `${s.nomeServico} - R$ ${s.preco}`,
    'Servico'
  );
  renderizarLista(
    'listaServicos',
    servicosOrdenados,
    (s) => `<strong>${s.idServico} - ${s.nomeServico}</strong><small>R$ ${s.preco} | ${valorOuVazio(s.tempoMin)} min</small>`,
    preencherServico,
    (s) => excluirRegistro(`/servicos/${s.idServico}`, carregarServicos, limparServico)
  );
}

async function carregarVeiculosCrud() {
  estado.veiculosCrud = await buscarJson('/veiculos');
  renderizarLista(
    'listaVeiculos',
    estado.veiculosCrud,
    (v) => `<strong>${v.placa} - ${v.modelo}</strong><small>Dono ID ${v.idCliente} | ${valorOuVazio(v.cor)} | ${valorOuVazio(v.ano)}</small>`,
    preencherVeiculo,
    (v) => excluirRegistro(`/veiculos/${encodeURIComponent(v.placa)}/${v.idCliente}`, atualizarVeiculos, limparVeiculo)
  );
}

async function carregarFuncionarios() {
  estado.funcionarios = await buscarJson('/funcionarios');
  const funcionariosOrdenados = [...estado.funcionarios].sort((a, b) => b.idFuncionario - a.idFuncionario);
  preencherSelect(
    'idFuncionarioAtendimento',
    estado.funcionarios,
    (f) => f.idFuncionario,
    (f) => `${f.nome} - ${valorOuVazio(f.cargo)}`,
    'Funcionario responsavel'
  );
  renderizarLista(
    'listaFuncionarios',
    funcionariosOrdenados,
    (f) => `<strong>${f.idFuncionario} - ${f.nome}</strong><small>${valorOuVazio(f.cargo)} | supervisor ${valorOuVazio(f.idSupervisor)}</small>`,
    preencherFuncionario,
    (f) => excluirRegistro(`/funcionarios/${f.idFuncionario}`, carregarFuncionarios, limparFuncionario)
  );
}

async function carregarAtendimentos() {
  estado.atendimentos = await buscarJson('/atendimentos');
  preencherSelectAtendimentosAvaliacao();
  renderizarLista(
    'listaAtendimentos',
    estado.atendimentos,
    (a) => `<strong>${a.idAtendimento} - ${a.placaVeiculo}</strong><small>cliente ${a.idClienteVeiculo} | servico ${a.idServico} | funcionario ${valorOuVazio(a.idFuncionario)} | ${a.data} ${String(a.hora).slice(0, 5)} | ${a.status}</small>`,
    preencherAtendimento,
    (a) => excluirRegistro(`/atendimentos/${a.idAtendimento}`, carregarAtendimentos, limparAtendimento)
  );
}

async function carregarAvaliacoes() {
  estado.avaliacoes = await buscarJson('/avaliacoes');
  preencherSelectAtendimentosAvaliacao();
  renderizarLista(
    'listaAvaliacoes',
    estado.avaliacoes,
    (a) => {
      const cliente = clientePorId(a.idCliente);
      return `<strong>${a.idAvaliacao} - Nota ${a.nota}</strong><small>${cliente ? cliente.nome : `Cliente ${a.idCliente}`} | atendimento ${a.idAtendimento} | ${valorOuVazio(a.data)}</small>`;
    },
    preencherAvaliacao,
    (a) => excluirRegistro(`/avaliacoes/${a.idAvaliacao}`, carregarAvaliacoes, limparAvaliacao)
  );
}

async function carregarVeiculos() {
  estado.veiculos = await buscarJson('/relatorios/opcoes-veiculos');
  preencherSelect(
    'veiculoAtendimento',
    estado.veiculos,
    (v) => `${v.placa}|${v.id_cliente}`,
    (v) => `${v.placa} - ${v.modelo} (${v.cliente})`,
    'Veiculo'
  );
}

async function atualizarVeiculos() {
  await carregarVeiculosCrud();
  await carregarVeiculos();
}

async function excluirRegistro(url, recarregar, limpar) {
  if (!confirm('Deseja excluir este registro?')) {
    return;
  }

  await enviarJson(url, 'DELETE');
  limpar();
  await recarregar();
  avisar('Registro excluido.');
}

function instalarCadastros() {
  document.querySelectorAll('[data-toggle-form]').forEach((botao) => {
    botao.addEventListener('click', () => alternarElemento(botao.dataset.toggleForm));
  });

  document.querySelectorAll('[data-toggle-lista]').forEach((botao) => {
    botao.addEventListener('click', () => alternarElemento(botao.dataset.toggleLista));
  });

  $('formCliente').addEventListener('submit', async (evento) => {
    evento.preventDefault();
    const id = $('idCliente').value;
    const cliente = {
      nome: $('nome').value,
      cpf: $('cpf').value,
      email: $('email').value,
      enderecoRua: $('enderecoRua').value,
      enderecoBairro: $('enderecoBairro').value,
      enderecoCidade: $('enderecoCidade').value
    };
    try {
      await enviarJson(id ? `/clientes/${id}` : '/clientes', id ? 'PUT' : 'POST', cliente);
      limparCliente();
      await carregarClientes();
      avisar('Cliente salvo.');
    } catch (erro) {
      tratarErro('salvar cliente', erro);
    }
  });

  $('formServico').addEventListener('submit', async (evento) => {
    evento.preventDefault();
    const id = $('idServico').value;
    const servico = {
      nomeServico: $('nomeServico').value,
      preco: Number($('preco').value),
      tempoMin: numeroOuNull($('tempoMin').value),
      descricao: $('descricao').value
    };
    try {
      await enviarJson(id ? `/servicos/${id}` : '/servicos', id ? 'PUT' : 'POST', servico);
      limparServico();
      await carregarServicos();
      avisar('Servico salvo.');
    } catch (erro) {
      tratarErro('salvar servico', erro);
    }
  });

  $('formFuncionario').addEventListener('submit', async (evento) => {
    evento.preventDefault();
    const id = $('idFuncionario').value;
    const funcionario = {
      nome: $('nomeFuncionario').value,
      cargo: $('cargoFuncionario').value,
      idSupervisor: numeroOuNull($('idSupervisor').value)
    };
    try {
      await enviarJson(id ? `/funcionarios/${id}` : '/funcionarios', id ? 'PUT' : 'POST', funcionario);
      limparFuncionario();
      await carregarFuncionarios();
      avisar('Funcionario salvo.');
    } catch (erro) {
      tratarErro('salvar funcionario', erro);
    }
  });

  $('formVeiculo').addEventListener('submit', async (evento) => {
    evento.preventDefault();
    const placaOriginal = $('placaVeiculoOriginal').value;
    const idClienteOriginal = $('idClienteVeiculoOriginal').value;
    const veiculo = {
      placa: $('placaVeiculoCadastro').value.toUpperCase(),
      idCliente: Number($('clienteVeiculoCadastro').value),
      modelo: $('modeloVeiculo').value,
      cor: $('corVeiculo').value,
      ano: numeroOuNull($('anoVeiculo').value)
    };
    try {
      const editando = placaOriginal && idClienteOriginal;
      const url = editando
        ? `/veiculos/${encodeURIComponent(placaOriginal)}/${idClienteOriginal}`
        : '/veiculos';
      await enviarJson(url, editando ? 'PUT' : 'POST', veiculo);
      limparVeiculo();
      await atualizarVeiculos();
      avisar('Veiculo salvo.');
    } catch (erro) {
      tratarErro('salvar veiculo', erro);
    }
  });

  $('formAtendimento').addEventListener('submit', async (evento) => {
    evento.preventDefault();
    const id = $('idAtendimento').value;
    const [placaVeiculo, idClienteVeiculo] = $('veiculoAtendimento').value.split('|');
    const atendimento = {
      placaVeiculo,
      idClienteVeiculo: Number(idClienteVeiculo),
      idServico: Number($('idServicoAtendimento').value),
      idFuncionario: Number($('idFuncionarioAtendimento').value),
      data: $('dataAtendimento').value,
      hora: $('horaAtendimento').value,
      status: $('statusAtendimento').value
    };
    try {
      await enviarJson(id ? `/atendimentos/${id}` : '/atendimentos', id ? 'PUT' : 'POST', atendimento);
      limparAtendimento();
      await carregarAtendimentos();
      await carregarAvaliacoes();
      await carregarLogs();
      avisar('Atendimento salvo.');
    } catch (erro) {
      tratarErro('salvar atendimento', erro);
    }
  });

  $('formAvaliacao').addEventListener('submit', async (evento) => {
    evento.preventDefault();
    atualizarClienteDaAvaliacao();
    const idAvaliacao = $('idAvaliacao').value;
    const avaliacao = {
      idCliente: Number($('idClienteAvaliacao').value),
      idAtendimento: Number($('idAtendimentoAvaliacao').value),
      nota: Number($('notaAvaliacao').value),
      comentarios: $('comentariosAvaliacao').value,
      data: $('dataAvaliacao').value
    };
    try {
      const url = idAvaliacao ? `/avaliacoes/${idAvaliacao}` : '/avaliacoes';
      await enviarJson(url, idAvaliacao ? 'PUT' : 'POST', avaliacao);
      limparAvaliacao();
      await carregarAvaliacoes();
      await carregarDashboard();
      avisar('Avaliacao salva.');
    } catch (erro) {
      tratarErro('salvar avaliacao', erro);
    }
  });

  $('idAtendimentoAvaliacao').addEventListener('change', atualizarClienteDaAvaliacao);

  $('cancelarCliente').addEventListener('click', limparCliente);
  $('cancelarServico').addEventListener('click', limparServico);
  $('cancelarVeiculo').addEventListener('click', limparVeiculo);
  $('cancelarFuncionario').addEventListener('click', limparFuncionario);
  $('cancelarAtendimento').addEventListener('click', limparAtendimento);
  $('cancelarAvaliacao').addEventListener('click', limparAvaliacao);
}

function montarUrlRelatorio(tipo) {
  if (tipo === 'consulta-faturamento') {
    return `/relatorios/${tipo}?minimo=${encodeURIComponent($('filtroFaturamento').value || 60)}`;
  }
  if (tipo === 'consulta-atendimentos-periodo') {
    const inicio = encodeURIComponent($('filtroInicio').value || '2026-04-10');
    const fim = encodeURIComponent($('filtroFim').value || '2026-04-20');
    return `/relatorios/${tipo}?inicio=${inicio}&fim=${fim}`;
  }
  return `/relatorios/${tipo}`;
}

function instalarRelatorios() {
  document.querySelectorAll('[data-relatorio]').forEach((botao) => {
    botao.addEventListener('click', async () => {
      const tipo = botao.dataset.relatorio;
      try {
        $('tituloRelatorio').textContent = botao.textContent;
        const dados = await buscarJson(montarUrlRelatorio(tipo));
        renderizarTabela('resultadoRelatorio', dados);
      } catch (erro) {
        tratarErro('carregar relatorio', erro);
      }
    });
  });
}

function instalarOperacoes() {
  $('formValorLiquido').addEventListener('submit', async (evento) => {
    evento.preventDefault();
    try {
      const id = encodeURIComponent($('funcaoIdAtendimento').value);
      const dados = await buscarJson(`/relatorios/funcao-valor-liquido?idAtendimento=${id}`);
      $('resultadoFuncao').textContent = `Valor final do atendimento ${dados.idAtendimento}: R$ ${valorOuVazio(dados.valorLiquido)}`;
    } catch (erro) {
      tratarErro('executar funcao de valor liquido', erro);
    }
  });

  $('formSituacaoAvaliacao').addEventListener('submit', async (evento) => {
    evento.preventDefault();
    try {
      const idAtendimento = encodeURIComponent($('funcaoAvaliacaoAtendimento').value);
      const dados = await buscarJson(`/relatorios/avaliacao-atendimento?idAtendimento=${idAtendimento}`);
      if (!dados.length) {
        $('resultadoFuncao').textContent = `O atendimento ${idAtendimento} ainda nao possui avaliacao cadastrada.`;
        return;
      }
      const avaliacao = dados[0];
      $('resultadoFuncao').textContent =
        `${avaliacao.cliente} avaliou o atendimento ${avaliacao.id_atendimento} com nota ${avaliacao.nota}. ` +
        `Classificacao: ${avaliacao.classificacao}. Comentario: ${valorOuVazio(avaliacao.comentarios)}`;
    } catch (erro) {
      tratarErro('buscar avaliacao do atendimento', erro);
    }
  });

  $('formAtualizarStatus').addEventListener('submit', async (evento) => {
    evento.preventDefault();
    try {
      const id = encodeURIComponent($('procIdAtendimento').value);
      const status = encodeURIComponent($('procStatus').value);
      const dados = await enviarJson(`/relatorios/procedimento-atualizar-status?idAtendimento=${id}&status=${status}`, 'POST');
      $('resultadoProcedure').textContent = dados.mensagem;
      await carregarAtendimentos();
      await carregarLogs();
    } catch (erro) {
      tratarErro('executar procedure de status', erro);
    }
  });

  $('botaoRecalcular').addEventListener('click', async () => {
    try {
      const dados = await enviarJson('/relatorios/procedimento-recalcular-pagamentos', 'POST');
      $('resultadoProcedure').textContent = dados.mensagem;
      await carregarLogs();
      await carregarDashboard();
    } catch (erro) {
      tratarErro('executar procedure com cursor', erro);
    }
  });

  $('botaoLogs').addEventListener('click', carregarLogs);
  $('botaoDashboard').addEventListener('click', carregarDashboard);

  const botaoAplicarFiltros = $('botaoAplicarFiltros');
  if (botaoAplicarFiltros) {
    botaoAplicarFiltros.addEventListener('click', carregarDashboard);
  }

  ['dashInicio', 'dashFim', 'dashGranularidade', 'dashLimiteServicos', 'dashLimiteClientes'].forEach((id) => {
    const elemento = $(id);
    if (!elemento) {
      return;
    }
    elemento.addEventListener('change', carregarDashboard);
  });
}

async function carregarLogs() {
  try {
    const dados = await buscarJson('/relatorios/logs');
    renderizarTabela('resultadoLogs', dados);
  } catch (erro) {
    tratarErro('carregar logs', erro);
  }
}

function filtrosDashboard() {
  return {
    inicio: $('dashInicio').value || '2026-04-01',
    fim: $('dashFim').value || '2026-04-30',
    granularidade: $('dashGranularidade').value || 'dia',
    limiteServicos: $('dashLimiteServicos').value || 10,
    limiteClientes: $('dashLimiteClientes').value || 10
  };
}

function montarParametros(parametros) {
  return Object.entries(parametros)
    .filter(([, valor]) => valor !== undefined && valor !== null && valor !== '')
    .map(([chave, valor]) => `${encodeURIComponent(chave)}=${encodeURIComponent(valor)}`)
    .join('&');
}

function renderizarIndicadores(container, itens) {
  const elemento = $(container);
  elemento.innerHTML = '';
  itens.forEach((item) => {
    const card = document.createElement('div');
    card.className = `indicador ${item.classe || ''}`.trim();
    card.innerHTML = `
      <span>${item.titulo}</span>
      <strong>${item.valor}</strong>
      ${item.detalhe ? `<small class="indicador-detalhe">${item.detalhe}</small>` : ''}
    `;
    elemento.appendChild(card);
  });
}

function renderizarIndicadoresResumo(resumo) {
  renderizarIndicadores('indicadoresDashboard', [
    { titulo: 'Total de clientes', valor: formatarNumero(resumo.total_clientes) },
    { titulo: 'Total de funcionarios', valor: formatarNumero(resumo.total_funcionarios) },
    { titulo: 'Total de servicos', valor: formatarNumero(resumo.total_servicos) },
    { titulo: 'Total de veiculos', valor: formatarNumero(resumo.total_veiculos) },
    { titulo: 'Atendimentos (total)', valor: formatarNumero(resumo.total_atendimentos), detalhe: `${formatarNumero(resumo.atendimentos_finalizados)} finalizados` },
    { titulo: 'Atendimentos no periodo', valor: formatarNumero(resumo.atendimentos_periodo) },
    { titulo: 'Faturamento liquido', valor: formatarMoeda(resumo.faturamento_periodo), detalhe: `descontos ${formatarMoeda(resumo.descontos_periodo)}`, classe: 'destaque-verde' },
    { titulo: 'Ticket medio', valor: formatarMoeda(resumo.ticket_medio), classe: 'destaque-verde' },
    { titulo: 'Taxa de conclusao', valor: `${formatarNumero(resumo.taxa_conclusao)}%`, classe: 'destaque-azul' },
    { titulo: 'Total de avaliacoes', valor: formatarNumero(resumo.total_avaliacoes), detalhe: `${formatarNumero(resumo.qtd_avaliacoes)} no periodo` }
  ]);

  renderizarIndicadores('estatisticasDashboard', [
    { titulo: 'Media das notas', valor: formatarNumero(resumo.media_nota), classe: 'destaque-azul' },
    { titulo: 'Mediana das notas', valor: formatarNumero(resumo.mediana_nota), classe: 'destaque-azul' },
    { titulo: 'Moda das notas', valor: formatarNumero(resumo.moda_nota), detalhe: `frequencia ${formatarNumero(resumo.moda_frequencia)}`, classe: 'destaque-azul' },
    { titulo: 'Variancia (populacional)', valor: formatarNumero(resumo.variancia_nota), classe: 'destaque-roxo' },
    { titulo: 'Desvio padrao', valor: formatarNumero(resumo.desvio_padrao_nota), classe: 'destaque-roxo' },
    { titulo: 'Nota minima / maxima', valor: `${formatarNumero(resumo.nota_minima)} - ${formatarNumero(resumo.nota_maxima)}` }
  ]);
}

function renderizarGraficoFaturamento(dados) {
  destruirGrafico('faturamento');
  const labels = dados.map((d) => d.nome_servico);
  const faturamentos = dados.map((d) => Number(d.faturamento_liquido));

  graficosDashboard.faturamento = new Chart($('graficoFaturamento'), {
    type: 'bar',
    data: {
      labels,
      datasets: [
        {
          label: 'Faturamento liquido (R$)',
          data: faturamentos,
          backgroundColor: coresParaItens(labels.length),
          borderRadius: 6
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false },
        tooltip: {
          callbacks: {
            label: (ctx) => `${ctx.dataset.label}: ${formatarMoeda(ctx.parsed.y)}`
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: { callback: (valor) => `R$ ${valor}` }
        }
      }
    }
  });
}

function renderizarGraficoStatus(dados) {
  destruirGrafico('status');
  const labels = dados.map((d) => d.status);
  const valores = dados.map((d) => Number(d.quantidade));

  graficosDashboard.status = new Chart($('graficoStatus'), {
    type: 'pie',
    data: {
      labels,
      datasets: [
        {
          data: valores,
          backgroundColor: coresParaItens(labels.length),
          borderWidth: 2,
          borderColor: '#ffffff'
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { position: 'bottom' },
        tooltip: {
          callbacks: {
            label: (ctx) => {
              const total = ctx.dataset.data.reduce((s, v) => s + v, 0);
              const percentual = total ? ((ctx.parsed / total) * 100).toFixed(1) : 0;
              return `${ctx.label}: ${ctx.parsed} (${percentual}%)`;
            }
          }
        }
      }
    }
  });
}

function renderizarGraficoTendencia(dados) {
  destruirGrafico('tendencia');
  const labels = dados.map((d) => d.periodo);
  const atendimentos = dados.map((d) => Number(d.total_atendimentos));
  const faturamentos = dados.map((d) => Number(d.faturamento));

  graficosDashboard.tendencia = new Chart($('graficoTendencia'), {
    type: 'line',
    data: {
      labels,
      datasets: [
        {
          label: 'Atendimentos',
          data: atendimentos,
          borderColor: '#2563eb',
          backgroundColor: 'rgba(37, 99, 235, 0.15)',
          fill: true,
          tension: 0.3,
          yAxisID: 'y'
        },
        {
          label: 'Faturamento (R$)',
          data: faturamentos,
          borderColor: '#15803d',
          backgroundColor: 'rgba(21, 128, 61, 0.12)',
          fill: true,
          tension: 0.3,
          yAxisID: 'y1'
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      interaction: { mode: 'index', intersect: false },
      plugins: { legend: { position: 'bottom' } },
      scales: {
        y: {
          type: 'linear',
          position: 'left',
          beginAtZero: true,
          title: { display: true, text: 'Atendimentos' }
        },
        y1: {
          type: 'linear',
          position: 'right',
          beginAtZero: true,
          grid: { drawOnChartArea: false },
          title: { display: true, text: 'Faturamento (R$)' }
        }
      }
    }
  });
}

function renderizarGraficoNotas(dados) {
  destruirGrafico('notas');
  const labels = dados.map((d) => `Nota ${d.nota}`);
  const frequencias = dados.map((d) => Number(d.frequencia));

  graficosDashboard.notas = new Chart($('graficoNotas'), {
    type: 'bar',
    data: {
      labels,
      datasets: [
        {
          label: 'Frequencia',
          data: frequencias,
          backgroundColor: dados.map((_, i) => cor(i + 3)),
          borderRadius: 4
        }
      ]
    },
    options: {
      indexAxis: 'y',
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        x: { beginAtZero: true, ticks: { stepSize: 1 } }
      }
    }
  });
}

function renderizarGraficoTopClientes(dados) {
  destruirGrafico('topClientes');
  const labels = dados.map((d) => d.nome);
  const gastos = dados.map((d) => Number(d.gasto_total));

  graficosDashboard.topClientes = new Chart($('graficoTopClientes'), {
    type: 'bar',
    data: {
      labels,
      datasets: [
        {
          label: 'Gasto total (R$)',
          data: gastos,
          backgroundColor: '#2563eb',
          borderRadius: 4
        }
      ]
    },
    options: {
      indexAxis: 'y',
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false },
        tooltip: {
          callbacks: {
            label: (ctx) => `${ctx.dataset.label}: ${formatarMoeda(ctx.parsed.x)}`
          }
        }
      },
      scales: {
        x: { beginAtZero: true, ticks: { callback: (v) => `R$ ${v}` } }
      }
    }
  });
}

async function carregarDashboard() {
  if (typeof Chart === 'undefined') {
    tratarErro('carregar dashboard', new Error('Biblioteca Chart.js nao foi carregada. Verifique a conexao com a internet.'));
    return;
  }

  const filtros = filtrosDashboard();
  const params = montarParametros({ inicio: filtros.inicio, fim: filtros.fim });
  const paramsTendencia = montarParametros({
    inicio: filtros.inicio,
    fim: filtros.fim,
    granularidade: filtros.granularidade
  });
  const paramsServicos = montarParametros({
    inicio: filtros.inicio,
    fim: filtros.fim,
    limite: filtros.limiteServicos
  });
  const paramsClientes = montarParametros({
    inicio: filtros.inicio,
    fim: filtros.fim,
    limite: filtros.limiteClientes
  });

  try {
    const [resumo, faturamento, status, tendencia, notas, topClientes] = await Promise.all([
      buscarJson(`/relatorios/dashboard-resumo?${params}`),
      buscarJson(`/relatorios/dashboard-faturamento-servico?${paramsServicos}`),
      buscarJson(`/relatorios/dashboard-status-atendimento?${params}`),
      buscarJson(`/relatorios/dashboard-tendencia?${paramsTendencia}`),
      buscarJson(`/relatorios/dashboard-distribuicao-notas?${params}`),
      buscarJson(`/relatorios/dashboard-top-clientes?${paramsClientes}`)
    ]);

    renderizarIndicadoresResumo(resumo);
    renderizarGraficoFaturamento(faturamento);
    renderizarGraficoStatus(status);
    renderizarGraficoTendencia(tendencia);
    renderizarGraficoNotas(notas);
    renderizarGraficoTopClientes(topClientes);
  } catch (erro) {
    tratarErro('carregar dashboard', erro);
  }
}

function instalarAbas() {
  document.querySelectorAll('.aba').forEach((aba) => {
    aba.addEventListener('click', () => {
      document.querySelectorAll('.aba').forEach((item) => item.classList.remove('ativa'));
      document.querySelectorAll('.painel').forEach((painel) => painel.classList.remove('ativo'));
      aba.classList.add('ativa');
      $(aba.dataset.alvo).classList.add('ativo');
    });
  });
}

async function iniciar() {
  instalarAbas();
  instalarCadastros();
  instalarRelatorios();
  instalarOperacoes();
  limparCliente();
  limparServico();
  limparVeiculo();
  limparFuncionario();
  limparAtendimento();
  limparAvaliacao();
  ['formCliente', 'formServico', 'formVeiculo', 'formFuncionario', 'formAtendimento', 'formAvaliacao'].forEach((id) => {
    $(id).classList.add('oculto');
  });

  try {
    await Promise.all([
      carregarClientes(),
      carregarServicos(),
      carregarVeiculosCrud(),
      carregarFuncionarios(),
      carregarAtendimentos(),
      carregarAvaliacoes(),
      carregarVeiculos(),
      carregarLogs(),
      carregarDashboard()
    ]);
  } catch (erro) {
    tratarErro('carregar dados iniciais', erro);
  }
}

iniciar();
