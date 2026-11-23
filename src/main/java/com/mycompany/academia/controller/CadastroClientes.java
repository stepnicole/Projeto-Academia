package com.mycompany.academia.controller;

import com.mycompany.academia.model.Cliente;
import com.mycompany.academia.model.Extrato;
import com.mycompany.academia.model.Identificador;
import com.mycompany.academia.model.Pessoa;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Cadastro de clientes (incluir, editar,
 * remover, buscar, listar) e emissão da conta do cliente.
 *
 * @author Ste
 */
public class CadastroClientes {

    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Sessao sessao;
    private final List<Cliente> clientes;

    /**
     * @param sessao sessão usada para checar o login
     */
    public CadastroClientes(Sessao sessao) {
        this.sessao = sessao;
        this.clientes = new ArrayList<>();
    }

    /**
     * Inclui um cliente com id definido automaticamente (próximo id livre).
     *
     * @param nome          nome
     * @param endereco      endereço
     * @param telefone      telefone
     * @param email         e-mail
     * @param cpf           CPF (é pseudo-anonimizado)
     * @param cartaoCredito número do cartão
     * @return o cliente criado
     */
    public Cliente cadastrar(String nome, String endereco, String telefone,
                             String email, String cpf, String cartaoCredito) {
        sessao.exigirLogin();
        return cadastrar(Identificador.proximoId(clientes), nome, endereco, telefone,
                email, cpf, cartaoCredito);
    }

    /**
     * Inclui um cliente com id escolhido pelo usuário.
     *
     * @param id            id desejado (positivo e livre)
     * @param nome          nome
     * @param endereco      endereço
     * @param telefone      telefone
     * @param email         e-mail
     * @param cpf           CPF
     * @param cartaoCredito número do cartão
     * @return o cliente criado
     * @throws IllegalArgumentException se o id já estiver em uso
     */
    public Cliente cadastrar(int id, String nome, String endereco, String telefone,
                             String email, String cpf, String cartaoCredito) {
        sessao.exigirLogin();
        if (id <= 0 || buscar(id) != null) {
            throw new IllegalArgumentException("Id de cliente inválido ou já em uso: " + id);
        }
        Cliente c = new Cliente(id, nome, endereco, telefone, email,
                Pessoa.anonimizarCpf(cpf), cartaoCredito);
        clientes.add(c);
        return c;
    }

    /**
     * Edita um cliente. Parâmetros {@code null} mantêm o valor atual.
     *
     * @param id            id do cliente
     * @param nome          novo nome ou null
     * @param endereco      novo endereço ou null
     * @param telefone      novo telefone ou null
     * @param email         novo e-mail ou null
     * @param cartaoCredito novo cartão ou null
     * @return false se o cliente não existe
     */
    public boolean editar(int id, String nome, String endereco, String telefone,
                          String email, String cartaoCredito) {
        sessao.exigirLogin();
        Cliente c = buscar(id);
        if (c == null) {
            return false;
        }
        if (nome != null) c.setNome(nome);
        if (endereco != null) c.setEndereco(endereco);
        if (telefone != null) c.setTelefone(telefone);
        if (email != null) c.setEmail(email);
        if (cartaoCredito != null) c.setCartaoCredito(cartaoCredito);
        return true;
    }

    /**
     * Remove o cliente do cadastro. Para também cancelar os agendamentos
     * pendentes dele, use {@code Sistema.removerCliente}, que coordena os dois
     * serviços.
     *
     * @param id id do cliente
     * @return true se removeu
     */
    public boolean remover(int id) {
        sessao.exigirLogin();
        return clientes.remove(buscar(id));
    }

    /**
     * @param id id do cliente
     * @return o cliente ou null
     */
    public Cliente buscar(int id) {
        for (Cliente c : clientes) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    /**
     * @param texto parte do nome
     * @return clientes cujo nome contém o texto
     */
    public List<Cliente> buscarPorNome(String texto) {
        sessao.exigirLogin();
        List<Cliente> lista = new ArrayList<>();
        for (Cliente c : clientes) {
            if (c.getNome().toLowerCase().contains(texto.toLowerCase())) {
                lista.add(c);
            }
        }
        return lista;
    }

    /**
     * @param comparador critério de ordenação (ex.: {@code new ClientePorNome()})
     * @return cópia da lista ordenada
     */
    public List<Cliente> listar(Comparator<Cliente> comparador) {
        sessao.exigirLogin();
        List<Cliente> copia = new ArrayList<>(clientes);
        copia.sort(comparador);
        return copia;
    }

    /** @return texto com os dados de todos os clientes */
    public String imprimirDados() {
        sessao.exigirLogin();
        StringBuilder sb = new StringBuilder("--- Clientes cadastrados ---\n");
        for (Cliente c : clientes) {
            sb.append(c).append('\n');
        }
        return sb.toString();
    }

    /**
     * Emite a "conta" do cliente: todos os extratos e o total gasto.
     *
     * @param idCliente cliente
     * @return texto da conta
     */
    public String emitirConta(int idCliente) {
        sessao.exigirLogin();
        Cliente c = buscar(idCliente);
        if (c == null) {
            return "Cliente " + idCliente + " não encontrado.";
        }
        StringBuilder sb = new StringBuilder("===== CONTA DE " + c.getNome().toUpperCase() + " =====\n");
        for (Extrato e : c.getExtratos()) {
            sb.append(String.format("%s  %-30s R$ %9.2f%n",
                    e.getDataHora().toLocalDate().format(DATA_BR), e.getTipo(), e.getValorTotal()));
        }
        sb.append(String.format("TOTAL GASTO NA ACADEMIA:              R$ %9.2f%n", c.calcularTotalConta()));
        return sb.toString();
    }

    /** @return todos os clientes (somente leitura) */
    public List<Cliente> getTodos() {
        return Collections.unmodifiableList(clientes);
    }

    /**
     * Substitui os clientes em memória pelos lidos do arquivo.
     *
     * @param lidos clientes recuperados
     */
    public void carregar(List<Cliente> lidos) {
        clientes.clear();
        clientes.addAll(lidos);
    }

    @Override
    public String toString() {
        return "CadastroClientes{clientes=" + clientes.size() + "}";
    }
}