async function carregarClientes() {
    const resposta = await fetch('/clientes');
    const clientes = await resposta.json();
    const lista = document.getElementById('listaClientes');
    lista.innerHTML = '';
  
    clientes.forEach(cliente => {
      const li = document.createElement('li');
      li.textContent = `${cliente.idCliente} - ${cliente.nome} - ${cliente.cpf}`;
      lista.appendChild(li);
    });
  }
  
  async function carregarServicos() {
    const resposta = await fetch('/servicos');
    const servicos = await resposta.json();
    const lista = document.getElementById('listaServicos');
    lista.innerHTML = '';
  
    servicos.forEach(servico => {
      const li = document.createElement('li');
      li.textContent = `${servico.idServico} - ${servico.nomeServico} - R$ ${servico.preco}`;
      lista.appendChild(li);
    });
  }
  
  document.getElementById('formCliente').addEventListener('submit', async (e) => {
    e.preventDefault();
  
    const cliente = {
      nome: document.getElementById('nome').value,
      cpf: document.getElementById('cpf').value,
      email: document.getElementById('email').value,
      enderecoRua: document.getElementById('enderecoRua').value,
      enderecoBairro: document.getElementById('enderecoBairro').value,
      enderecoCidade: document.getElementById('enderecoCidade').value
    };
  
    await fetch('/clientes', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(cliente)
    });
  
    e.target.reset();
    carregarClientes();
  });
  
  document.getElementById('formServico').addEventListener('submit', async (e) => {
    e.preventDefault();
  
    const servico = {
      nomeServico: document.getElementById('nomeServico').value,
      preco: document.getElementById('preco').value,
      tempoMin: document.getElementById('tempoMin').value,
      descricao: document.getElementById('descricao').value
    };
  
    await fetch('/servicos', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(servico)
    });
  
    e.target.reset();
    carregarServicos();
  });
  
  carregarClientes();
  carregarServicos();