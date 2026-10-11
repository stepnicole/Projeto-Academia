package com.mycompany.academia;
/**
 *
 * @author ste
 */
import com.mycompany.academia.model.Agendamento;
import com.mycompany.academia.model.Aula;
import com.mycompany.academia.model.Cliente;
import com.mycompany.academia.model.Instrutor;
import com.mycompany.academia.model.Pagamento;
import com.mycompany.academia.model.Persistencia;
import com.mycompany.academia.model.TipoAgendamento;
import com.mycompany.academia.model.TipoAula;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Academia {

    public static void main(String[] args) throws IOException {
        List<Cliente> clientes = new ArrayList<>();
        Cliente c = new Cliente(1, "Ana", "Rua A", "3899999-0000",
                "ana@email.com", "xxx.123.xxx-45", "1234567812345678");
        c.adicionarAgendamento(10);
        clientes.add(c);

        Persistencia.salvarLista("dados/clientes.json", clientes);

        List<Cliente> lidos = Persistencia.carregarLista("dados/clientes.json", Cliente.class);
        System.out.println(lidos);
        
        Instrutor i = new Instrutor(4, "Marcos", "Rua D", "3866666-0000",
        "marcos@email.com", "xxx.321.xxx-09", TipoAula.SPINNING, 2500.0);
        System.out.println(i);

        List<Instrutor> instrutores = new ArrayList<>();
        instrutores.add(i);

        Persistencia.salvarLista("dados/instrutores.json", instrutores);

        List<Instrutor> instrutoresLidos = Persistencia.carregarLista("dados/instrutores.json", Instrutor.class);
        System.out.println(instrutoresLidos);
        
        Aula aula = new Aula(1, TipoAula.SPINNING, 1, 4, LocalDateTime.of(2026, 10, 20, 8, 0));
        System.out.println(aula);
        System.out.println("Tem vaga? " + aula.temVaga());

        for (int k = 0; k < 15; k++) {
        aula.ocuparVaga();
    }
        System.out.println("Lotada, tem vaga? " + aula.temVaga());      
        System.out.println("Ocupou mais uma? " + aula.ocuparVaga());    
        System.out.println("Liberou? " + aula.liberarVaga());           
        System.out.println(aula);

        List<Aula> aulas = new ArrayList<>();
        aulas.add(aula);
        Persistencia.salvarLista("dados/aulas.json", aulas);
        System.out.println(Persistencia.carregarLista("dados/aulas.json", Aula.class));
        
        List<LocalDate> datas = new ArrayList<>();
        datas.add(LocalDate.of(2026, 10, 30));  

        Agendamento ag = new Agendamento(1, 1, 1, datas, 80.0,
        TipoAgendamento.DIARIA, LocalDate.of(2026, 10, 11));
        System.out.println(ag);
        System.out.println("Prazo confirmação: " + ag.getPrazoConfirmacao());     // 2026-10-23
        System.out.println("Prazo cancelamento: " + ag.getPrazoCancelamento());   // 2026-10-27

        System.out.println(ag.confirmar(LocalDate.of(2026, 10, 20)));             
        System.out.println(ag.calcularReembolso(LocalDate.of(2026, 10, 25)));     
        System.out.println(ag.calcularReembolso(LocalDate.of(2026, 10, 28)));
        System.out.println(ag.cancelar(LocalDate.of(2026, 10, 25)));              
        System.out.println(ag.getStatus());                                       

        Agendamento ag2 = new Agendamento(2, 1, 1, datas, 80.0,
        TipoAgendamento.DIARIA, LocalDate.of(2026, 10, 11));
        System.out.println(ag2.confirmar(LocalDate.of(2026, 10, 24)));            
        System.out.println(ag2.expirar(LocalDate.of(2026, 10, 24)));              
        System.out.println(ag2.getStatus());                                      

        List<Agendamento> agendamentos = new ArrayList<>();
        agendamentos.add(ag);
        agendamentos.add(ag2);
        Persistencia.salvarLista("dados/agendamentos.json", agendamentos);
        System.out.println(Persistencia.carregarLista("dados/agendamentos.json", Agendamento.class));
        
        Agendamento teste = new Agendamento(3, 1, 1, datas, 80.0,
        TipoAgendamento.DIARIA, LocalDate.of(2026, 10, 11));
        System.out.println(Pagamento.debitar(1, teste, LocalDate.now()));  

        teste.confirmar(LocalDate.of(2026, 10, 20));
        Pagamento cobranca = Pagamento.debitar(1, teste, LocalDate.of(2026, 10, 20));
        System.out.println(cobranca);                                        

        double devolver = teste.cancelar(LocalDate.of(2026, 10, 25));       
        Pagamento reembolso = Pagamento.reembolsar(2, teste, devolver, LocalDate.of(2026, 10, 25));
        System.out.println(reembolso);                                       

        List<Pagamento> pagamentos = new ArrayList<>();
        pagamentos.add(cobranca);
        pagamentos.add(reembolso);
        Persistencia.salvarLista("dados/pagamentos.json", pagamentos);
        System.out.println(Persistencia.carregarLista("dados/pagamentos.json", Pagamento.class));
    }
}