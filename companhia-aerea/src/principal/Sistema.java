package principal;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.swing.*;

import aviao.Aviao;
import aviao.Voo;
import exceptions.AssentoOcupadoException;
import usuario.Atendente;
import usuario.Passageiro;
import usuario.Usuario;
import view.MenuPrincipal.ViewMenuPrincipal;

public class Sistema {
	
	public static final int qtdAvioes = 4;
	private static ArrayList<Aviao> avioes;
	private static ArrayList<Usuario> users;
	private static GerenciadorArquivos<Usuario> gerenciadorUsuarios;
	private static GerenciadorArquivos<Voo> gerenciadorVoos;
	private static GerenciadorLog log;
	private static Usuario usuarioAtual;
	
	public static void main(String[] args) {
		try {
			log = GerenciadorLog.getInstancia();
			gerenciadorUsuarios = new GerenciadorArquivos<>("users", Usuario.class);
			gerenciadorVoos = new GerenciadorArquivos<>("voos", Voo.class);
		} catch (IOException e) {
			System.out.println("Erro ao carregar um dos arquivos.");
			e.printStackTrace();
			
			log.registrarAcao("Erro ao carregar um dos arquivos, causa:");
			log.registrarAcao(e.getStackTrace().toString());
			
			desliga();
		} catch (Exception e) {
			System.out.println("Erro inesperado");
			e.printStackTrace();
			
			log.registrarAcao("Erro inesperado:");
			log.registrarAcao(e.getStackTrace().toString());
			
			desliga();
		}
		
		log.registrarAcao("Sistema iniciado.");
		carregaUsuarios();
		carregaAvioes();
		new ViewMenuPrincipal().setVisible(true);
		//menuPrincipal();
	}
	
	private static void salvaUsuarios() throws IOException {
		try {
			gerenciadorUsuarios.salvaDados(users);
			log.registrarAcao("Usuarios salvos com sucesso.");
		} catch (IOException e) {
			System.out.println("Erro ao salvar usuarios.");
			e.printStackTrace();
			
			log.registrarAcao("Erro ao salvar usuarios, causa:");
			log.registrarAcao(e.getStackTrace().toString());
			
			throw new IOException(e.getMessage());
		} catch (Exception e) {
			System.out.println("Erro inesperado");
			e.printStackTrace();
			
			log.registrarAcao("Erro inesperado:");
			log.registrarAcao(e.getStackTrace().toString());
			
			throw new RuntimeException();
		}
	}
	
	private static void carregaUsuarios() {
		if(users == null) {
			try {
				users = (ArrayList<Usuario>) gerenciadorUsuarios.carregaDados();
				log.registrarAcao("Usuarios carregados com sucesso.");
			} catch (IOException e) {
				users = null;
				
				System.out.println("Erro ao ler usuarios");
				e.printStackTrace();
				
				log.registrarAcao("Erro ao ler usuarios, causa:");
				log.registrarAcao(e.getStackTrace().toString());
			} catch (Exception e) {
				System.out.println("Erro inesperado");
				e.printStackTrace();
				
				log.registrarAcao("Erro inesperado:");
				log.registrarAcao(e.getStackTrace().toString());
				
				throw new RuntimeException();
			}
		}
	}
	
	private static void carregaAvioes() {
		if(avioes == null) {
			try {
				ArrayList<Voo> voos = (ArrayList<Voo>) gerenciadorVoos.carregaDados();
				log.registrarAcao("Voos carregados com sucesso.");
				
				avioes = new ArrayList<>();
				
				for(int i = 0; i < qtdAvioes; i++) {
					avioes.add(new Aviao("Aviao" + i));
				}
				
				for(Voo voo : voos) {
					if(avioes.contains(voo.getAviao())) {
						int index = avioes.indexOf(voo.getAviao());
						avioes.get(index).addVoo(voo);				
					}
				}
			} catch (IOException e) {
				System.out.println("Erro ao ler voos.");
				e.printStackTrace();
				
				log.registrarAcao("Erro ao ler voos, causa:");
				log.registrarAcao(e.getStackTrace().toString());
			} catch (Exception e) {
				System.out.println("Erro inesperado");
				e.printStackTrace();
				
				log.registrarAcao("Erro inesperado:");
				log.registrarAcao(e.getStackTrace().toString());
				
				throw new RuntimeException();
			}
		}
	}
	
	private static void salvaVoos() throws IOException {
		if(avioes != null) {
			ArrayList<Voo> voos = new ArrayList<>();
			
			for(Aviao aviao : avioes) {
				if(aviao.getVoos() != null) {
					for(Voo voo : aviao.getVoos()) {
						voos.add(voo);
					}
				}
			}
			
			try {
				gerenciadorVoos.salvaDados(voos);
				log.registrarAcao("Voos salvos com sucesso");
			} catch (IOException e) {
				System.out.println("Erro ao salvar voos");
				e.printStackTrace();
				
				log.registrarAcao("Erro ao salvar voos, causa:");
				log.registrarAcao(e.getStackTrace().toString());
				
				throw new IOException(e.getMessage());
			} catch (Exception e) {
				System.out.println("Erro inesperado");
				e.printStackTrace();
				
				log.registrarAcao("Erro inesperado:");
				log.registrarAcao(e.getStackTrace().toString());
				
				throw new RuntimeException();
			}
		}
	}
	
	public static void desliga() {
		String msg;
		msg = "Desconectando.";
		mostraAviso(msg, JOptionPane.INFORMATION_MESSAGE);
		log.registrarAcao("Desligando sistema.");
		
		try {
			salvaUsuarios();
			salvaVoos();
		} catch (IOException e) {
			log.registrarAcao("Sistema desligou com erros.");
		}
		
		log.registrarAcao("Sistema desligou sem erros.");
		
		try {
			log.close();
		} catch(Exception e) {
			msg = "Erro ao fechar log.";
			mostraAviso(msg, JOptionPane.ERROR_MESSAGE);
		}	
	}	
	
	private static Usuario buscaUsuario(String nome) {
		for(Usuario u : users) {
			if(u.getNome().equals(nome)) {
				return u;
			}
		}
		return null;
	}
	
	
	
	private static void mensagemBoasVindas() {
        String html = "<html><body>"
        			+ "<h3>Seja bem vindo(a) a UFABC Airlines!</h3>"
					+ "<pre>            ______                                        <br>"
					+ "            _\\ _~-\\___                                       <br>"
					+ "    =  = ==(___UFABC__D                                        <br>"
					+ "                \\_____\\___________________,-~~~~~~~`-.._     <br>"
					+ "                /     o O o o o o O O o o o o o o O o  |\\_    <br>"
					+ "                `~-.__        ___..----..                  )   <br>"
					+ "                      `---~~\\___________/------------`````    <br>"
					+ "                      =  ===(_________D                        <br>"
					+ "<br><br><p width='400px' align='center'> Clique em OK para continuar"
					+ "</body></html>\n"
					+ "\n";
		
		JOptionPane.showMessageDialog(null, html, "Bem-vindo(a)", JOptionPane.PLAIN_MESSAGE);
	}
	
	private static void menuPrincipal() {
		mensagemBoasVindas();
		int escolha = 0;
		String msg;
		while(escolha != 5) {
			msg = "Escolha uma opcao:\n"
				+ "|- 1. Cadastrar um novo usuario\n"
				+ "|- 2. Remover um usuario\n"
				+ "|- 3. Exibir usuarios cadastrados\n"
				+ "|- 4. Fazer login (eh necessario para buscar os voos)\n"
				+ "|- 5. Sair\n";
			
			escolha = Integer.parseInt(JOptionPane.showInputDialog(null, msg, "Menu Principal", JOptionPane.PLAIN_MESSAGE));
			
			switch(escolha) {
			case 1:
				menuCadastroUsuario();
				break;
			case 2:
				menuRemoveUsuario();
				break;
			case 3:
				if(users.size() == 0) {
					msg = "Nao ha usuarios cadastrados";
					JOptionPane.showMessageDialog(null, msg, "Erro", JOptionPane.ERROR_MESSAGE);
				} else {
					msg = "";
					for (Usuario u : users) { 
						msg += u + "\n";
					}
					JOptionPane.showMessageDialog(null, msg, "Usuarios", JOptionPane.PLAIN_MESSAGE);
				}
				break;
			case 4:
				menuLogin();
				break;
			case 5:
				break;
			default:
				msg = "Escolha invalida.";
				JOptionPane.showMessageDialog(null, msg, "Erro", JOptionPane.ERROR_MESSAGE);
				break;
			}
		}
		
		desliga();
	}

	private static void menuCadastroUsuario() {
		String msg;
		char tipoUser;
		String nomeUser;
		String idadeUser;
		Usuario novo;
		
		msg = "Digite o tipo de usuario(A para Atendente, P para passageiro), ou digite 'S' para voltar:";
		tipoUser = JOptionPane.showInputDialog(null, msg, "Cadastrar Usuario", JOptionPane.PLAIN_MESSAGE).toUpperCase().charAt(0);
		
		while(tipoUser != 'A' && tipoUser != 'P' && tipoUser != 'S') {
			msg = "Opcao invalida, digite A para Atendente, P para passageiro ou 'S' para voltar";
			tipoUser = JOptionPane.showInputDialog(null, msg, "Cadastrar Usuario", JOptionPane.PLAIN_MESSAGE).toUpperCase().charAt(0);
		}
		
		if(tipoUser == 'S') {
			return;
		}
		
		msg = "Digite nome do usuario, use '-' ou '_' no lugar de espacos, ou 'S' para voltar:";
		nomeUser = JOptionPane.showInputDialog(null, msg, "Cadastrar Usuario", JOptionPane.PLAIN_MESSAGE).replaceAll("[_-]", " ");
		
		if(nomeUser.toUpperCase().equals("S")) {
			return;
		}
		
		while(buscaUsuario(nomeUser) != null) {
			msg = "Nome ja esta sendo utilizado, digite um nome diferente ou 'S' para voltar";
			nomeUser = JOptionPane.showInputDialog(null, msg, "Cadastrar Usuario", JOptionPane.PLAIN_MESSAGE).replaceAll("[_-]", " ");
			
			if(nomeUser.toUpperCase().equals("S")) {
				return;
			}
		}
		
		if(tipoUser == 'A') {
			novo = new Atendente(nomeUser);
		} else {
			msg = "Digite a idade do usuario, ou 'S' para voltar:";
			idadeUser = JOptionPane.showInputDialog(null, msg, "Cadastrar Usuario", JOptionPane.PLAIN_MESSAGE);
			
			if(idadeUser.toUpperCase().equals("S"))
				return;
				
			while(Integer.parseInt(idadeUser) < 0) {
				msg = "Idade inválida!";
				idadeUser = JOptionPane.showInputDialog(null, msg, "Cadastrar Usuario", JOptionPane.PLAIN_MESSAGE);
				if(idadeUser.toUpperCase().equals("S"))
					return;
			}
			novo = new Passageiro(nomeUser, Integer.parseInt(idadeUser));
		}
		
		users.add(novo);
		msg = "Usuario adicionado com sucesso";
		JOptionPane.showMessageDialog(null, msg, "Sucesso", JOptionPane.PLAIN_MESSAGE);
		log.registrarAcao("Novo usuario adicionado: " + nomeUser);
	}
	
	private static void menuRemoveUsuario() {
		String msg;
		msg = "Digite o nome do usuario que sera removido, use '-' ou '_' no lugar de espacos, ou 'S' para voltar:";
		String nome = JOptionPane.showInputDialog(null, msg, "Remover Usuario", JOptionPane.PLAIN_MESSAGE).replaceAll("[_-]", " ");
		
		if(nome.toUpperCase().equals("S")) {
			return;
		}
		
		Usuario user = buscaUsuario(nome);
		
		while(user == null) {
			msg = "Nao existe nenhum usuario com esse nome, digite um nome diferente ou 'S' para voltar";
			nome = JOptionPane.showInputDialog(null, msg, "Remover Usuario", JOptionPane.PLAIN_MESSAGE).replaceAll("[_-]", " ");
			
			if(nome.toUpperCase().equals("S")) {
				return;
			}
		}
		
		users.remove(user);
		msg = "Usuario removido com sucesso";
		JOptionPane.showMessageDialog(null, msg, "Sucesso", JOptionPane.PLAIN_MESSAGE);
		log.registrarAcao("Usurario removido: " + nome);
		
	}
	
	private static void menuLogin() {
		String msg;
		String nome = "";
		
		while(nome != null && !nome.toUpperCase().equals("S")) {
			msg = "Digite o seu nome, use '-' ou '_' no lugar de espacos, ou digite 'S' para voltar:";
			nome = JOptionPane.showInputDialog(null, msg, "Logar", JOptionPane.PLAIN_MESSAGE).replaceAll("[_-]", " ");
			Usuario user = buscaUsuario(nome);
			
			if(!nome.toUpperCase().equals("S") && user != null) {
				if(user instanceof Atendente) {
					menuAtendente((Atendente) user);
					return;
				} else {
					menuPassageiro(user);
					return;
				}
			} else if (!nome.toUpperCase().equals("S")) {
				msg = "Usuario nao cadastrado.";
				JOptionPane.showMessageDialog(null, msg, "Erro", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private static void menuPassageiro(Usuario usuario) {
		String msg;
		msg = "Seja bem vindo(a) " + usuario.getNome();
		int escolha = 0;
		
		while(escolha != 6) {
			msg = "Escolha uma opcao:\n"
				+ "|- 1. Consultar reservas\n"
				+ "|- 2. Cancelar reserva\n"
				+ "|- 3. Pagar reserva\n"
				+ "|- 4. Procurar voo\n"
				+ "|- 5. Alterar nome\n"
				+ "|- 6. Voltar";
			
			
			escolha = Integer.parseInt(JOptionPane.showInputDialog(null, msg, "Menu de Passageiro", JOptionPane.PLAIN_MESSAGE));
			
			switch(escolha) {
			case 1:
				/*if(usuario.getReservas() == null || usuario.getReservas().size() == 0) {
					System.out.println("O usuario nao possui reservas");
				} else {
					usuario.getReservas().forEach(reserva -> System.out.println(reserva));
				}
				*/
				break;
			case 2:
				//menuCancelaReserva(usuario);
				break;
			case 3:
				//menuPagaReserva(usuario);
				break;
			case 4:
				menuBuscaVoo(usuario);
				break;
			case 5:
				menuMudaNome(usuario);
				break;
			case 6:
				break;
			default:
				msg = "Escolha invalida.";
				JOptionPane.showMessageDialog(null, msg, "Erro", JOptionPane.ERROR_MESSAGE);
				break;
			}
		}
	}
	
	private static void menuAtendente(Atendente atendente) {
		String msg;
		msg = "Seja bem vindo(a) " + atendente.getNome();
		int escolha = 0;
		
		while(escolha != 9) {
			 msg = 	  "Escolha uma opcao:\n"
					+ "|- 1. Consultar reservas\n"
					+ "|- 2. Cancelar reserva\n"
					+ "|- 3. Pagar reserva\n"
					+ "|- 4. Procurar voo\n"
					+ "|- 5. Alterar nome\n"
					+ "|- 6. Criar voo\n"
					+ "|- 7. Cancelar voo\n"
					+ "|- 8. Gerar relatorio de um voo\n"
					+ "|- 9. Voltar\n";
			
			escolha = Integer.parseInt(JOptionPane.showInputDialog(null, msg, "Menu de Atendente", JOptionPane.PLAIN_MESSAGE));
			
			switch(escolha) {
			case 1:
				/*if(atendente.getReservas() == null || atendente.getReservas().size() == 0) {
					System.out.println("O usuario nao possui reservas");
				} else {
					atendente.getReservas().forEach(reserva -> System.out.println(reserva));
				}
				*/
				break;
			case 2:
				//menuCancelaReserva(atendente);
				break;
			case 3:
				//menuPagaReserva(atendente);
				break;
			case 4:
				menuBuscaVoo(atendente);
				break;
			case 5:
				menuMudaNome(atendente);
				break;
			case 6:
				try {
					atendente.criaVoo(avioes);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
					return;
				}
				break;
			case 7:
				atendente.cancelaVoo(getVoosAtuais());
				break;
			case 8:
				atendente.geraRelatorio(getVoosAtuais());
				break;
			case 9:
				break;
			default:
				msg = "Escolha invalida.";
				JOptionPane.showMessageDialog(null, msg, "Erro", JOptionPane.ERROR_MESSAGE);
				break;
			}
		}
	}
	/*
	private static void menuCancelaReserva(Usuario usuario) {
		ArrayList<Reserva> reservas = usuario.getReservas();
		
		if(reservas != null && reservas.size() > 0) {
			int escolha;
			
			System.out.println("Escolha uma reserva para cancelar ou digite -1 para sair:");
			
			for(int i = 0; i < reservas.size(); i++) {
				System.out.println((i + 1) + "" + reservas.get(i));
			}
			
			escolha = sc.nextInt();
			
			while(escolha < -1 || escolha >= reservas.size()) {
				System.out.println("Escolha algo entre 1 e " + reservas.size());
				escolha = sc.nextInt();
			}
			
			if(escolha == -1) {
				return;
			}
			
			usuario.cancelaReserva(reservas.get(escolha - 1));
		} else {
			System.out.println("O usuario nao possui reservas");
		}
	}
	
	private static void menuPagaReserva(Usuario usuario) {
		ArrayList<Reserva> reservas = usuario.getReservas();
		
		if(reservas != null && reservas.size() > 0) {
			int escolha;
			
			System.out.println("Escolha uma reserva para pagar ou digite -1 para sair:");
			
			for(int i = 0; i < reservas.size(); i++) {
				System.out.println((i + 1) + "" + reservas.get(i));
			}
			
			escolha = sc.nextInt();
			
			while(escolha < -1 || escolha >= reservas.size()) {
				System.out.println("Escolha algo entre 1 e " + reservas.size());
				escolha = sc.nextInt();
			}
			
			if(escolha == -1) {
				return;
			}
			
			usuario.pagaReserva(reservas.get(escolha));
		} else {
			System.out.println("O usuario nao possui reservas");
		}
	}*/
	
	private static void menuBuscaVoo(Usuario usuario) {
		String msg;
		msg = "Digite um destino, use '_' ou '-' no lugar de espacos, ou 'S' para voltar: ";
		String destino = JOptionPane.showInputDialog(null, msg, "Busca de voos", JOptionPane.PLAIN_MESSAGE).replaceAll("[_-]", " ");
		final String destinoConst = destino;
		
		if(destino.toUpperCase().equals("S")) {
			return;
		}
		
		ArrayList<Voo> voosAtuais = getVoosAtuais();
		ArrayList<Voo> matches = (ArrayList<Voo>) voosAtuais.stream()
				.filter(voo -> voo.getDestino().equals(destinoConst) && voo.getAviao() != null)
				.collect(Collectors.toList());
		
		while(matches.size() == 0) {
			msg = "Nao ha voos para esse destino. Digite um novo destino para continuar buscando ou 'S' para sair";
			destino = JOptionPane.showInputDialog(null, msg).replaceAll("[_-]", " ");
			final String destinoFinal = destino;
			
			if(destino.toUpperCase().equals("S")) {
				return;
			}
			
			matches = (ArrayList<Voo>) voosAtuais.stream()
					.filter(voo -> voo.getDestino().equals(destinoFinal))
					.collect(Collectors.toList());
		}
		
		msg = "Os voos disponiveis para esse destino sao:\n"
			+ "Escolha um, ou digite -1 para sair:\n";
		
		for(int i = 0; i < matches.size(); i++) {
			msg += "\n" + i + " - " + matches.get(i);
		}
		
		int escolha = Integer.parseInt(JOptionPane.showInputDialog(null, msg, JOptionPane.PLAIN_MESSAGE));
		
		while(escolha < -1 || escolha >= matches.size()) {
			msg = "Escolha algo entre 1 e " + matches.size();
			escolha = Integer.parseInt(JOptionPane.showInputDialog(null, msg, JOptionPane.PLAIN_MESSAGE));
		}
		
		if(escolha == -1) {
			return;
		}
		
		menuReserva(matches.get(escolha),usuario);
	}
	
	private static void menuReserva(Voo voo, Usuario user) {
		
		String msg;
		
		msg = "Escolha um assento, ou digite 'S' para sair: ";
		String nAssento = JOptionPane.showInputDialog(null, msg, JOptionPane.PLAIN_MESSAGE);
		
		if(nAssento.toUpperCase().equals("S")) {
			return;
		}
		
		while (Integer.parseInt(nAssento)<0&&Integer.parseInt(nAssento)>110){
			msg = "Assento nao existe"; 
			nAssento = JOptionPane.showInputDialog(null, msg, JOptionPane.PLAIN_MESSAGE);
			if(nAssento.toUpperCase().equals("S")) {
				return;
			}
		}
		
		if(Integer.parseInt(nAssento)-1>20) {
			try {
				voo.getPoltronas().get(Integer.parseInt(nAssento)-1).reserva(user, user.getIdade(), false);
			} catch (NumberFormatException | AssentoOcupadoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			try {
				voo.getPoltronas().get(Integer.parseInt(nAssento)-1).reserva(user, user.getIdade(), true);
			} catch (NumberFormatException | AssentoOcupadoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		msg = "Assento reservado com sucesso";
		JOptionPane.showMessageDialog(null,msg);
		msg = "Deseja reservar outro assento, caso n�o digite 'S' para sair: ";
		String outra = JOptionPane.showInputDialog(null, msg, JOptionPane.PLAIN_MESSAGE);
		
		if(outra.toUpperCase().equals("S")) {
			return;
		}else menuReserva(voo,user);
		
	}
	
	private static void menuMudaNome(Usuario usuario) {
		String msg;
		msg = "Digite um novo nome, use '-' ou '_' no lugar de espacos, ou 'S' para voltar:";
		String novoNome = JOptionPane.showInputDialog(null, msg, "Mudar Nome", JOptionPane.PLAIN_MESSAGE).replaceAll("[_-]", " ");
		
		if(novoNome.toUpperCase().equals("S")) {
			return;
		}
		
		while(buscaUsuario(novoNome) != null) {
			msg = "Nome ja esta sendo utilizado, digite um nome diferente ou 'S' para voltar: ";
			novoNome = JOptionPane.showInputDialog(null, msg, "Mudar Nome", JOptionPane.PLAIN_MESSAGE).replaceAll("[_-]", " ");
			
			if(novoNome.toUpperCase().equals("S")) {
				return;
			}
		}
		msg = "Nome alterado com sucesso!";
		JOptionPane.showMessageDialog(null, msg, "Sucesso", JOptionPane.PLAIN_MESSAGE);
		usuario.setNome(novoNome);
	}
	
	public static Atendente validaCadastroAtendente(String nome) {
		if (nome.isBlank() || nome.isEmpty()) {
			throw new IllegalArgumentException("Nome inválido!");
		}
		if (buscaUsuario(nome) != null) {
			throw new IllegalArgumentException("Nome já está em uso!");
		}
		return new Atendente(nome);
	}
	
	public static void cadastraAtendente(Atendente atendente) {
		users.add(atendente);
		log.registrarAcao("Novo atendente adicionado: " + atendente.getNome());
	}
	
	public static Passageiro validaCadastroPassageiro(String nome, int idade) {
		if (nome.isBlank() || nome.isEmpty()) {
			throw new IllegalArgumentException("Nome inválido!");
		}
		if (buscaUsuario(nome) != null) {
			throw new IllegalArgumentException("Nome já está em uso!");
		}
		return new Passageiro(nome, idade);
	}
	
	public static void cadastraPassageiro(Passageiro passageiro) {
		users.add(passageiro);
		log.registrarAcao("Novo passageiro adicionado: " + passageiro.getNome());
	}
	
	public static String[] getNomeUsuarios() {
		String[] usuarios = new String[users.size()];
		for(Usuario u : users) {
			if (u instanceof Atendente) {
				usuarios[users.indexOf(u)] = "Atendente: " + u.getNome();
			} else {
				usuarios[users.indexOf(u)] = "Passageiro: " + u.getNome() + " | Idade: " + u.getIdade();
			}
		}
		return usuarios;
	}
	
	public static int getQtdUsuarios() {
		return users.size();
	}
	
	public static void removeUsuario(String nome) {
		Usuario user = buscaUsuario(nome);
		users.remove(user);
		mostraAviso("Usuario removido com sucesso", JOptionPane.PLAIN_MESSAGE);
		log.registrarAcao("Usurario removido: " + user.getNome());
	}
	
	public static boolean validaLogin(String nome) {
		Usuario user = buscaUsuario(nome);
		if (user == null) {
			throw new IllegalArgumentException("Usuário não cadastrado!");
		}
		return true;
	}
	
	public static String fazLogin(String nome) {
		Usuario user = buscaUsuario(nome);
		usuarioAtual = user;
		if (user instanceof Atendente) {
			return "Atendente";
		}
		return "Passageiro";
	}
	
	public static ArrayList<Voo> getVoosAtuais() {
		ArrayList<Voo> voos = new ArrayList<>();
		for(Aviao aviao : avioes) {
			if(aviao.getVoos() != null) {
				voos.addAll(aviao.getVoos());
			}
		}
		return voos;
	}
	
	public static int getQtdVoos() {
		return getVoosAtuais().size();
	}
	
	public static int getQtdAvioes() {
		return avioes.size();
	}
	
	public static ArrayList<Aviao> getAvioesDisponiveis(LocalDate data, LocalTime hora) {
		Voo voo = new Voo(data, hora, "", "", 0f, 0f);
		ArrayList<Aviao> disponiveis = (ArrayList<Aviao>) avioes.stream().filter(a -> a.verificaDisponibilidade(voo)).collect(Collectors.toList());
		return disponiveis;
	}
	
	public static Voo validaCriacaoVoo(String origem, String destino, LocalDate data, LocalTime hora, double precoPrimeiraClasse, double precoClasseEconomica, Aviao aviao) {
		if (data.isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("Data inválida!");
		}
		if (data == LocalDate.now() && hora.isBefore(LocalTime.now())) {
			throw new IllegalArgumentException("Hora inválida!");
		}
		if (origem.isBlank() || origem.isEmpty()) {
			throw new IllegalArgumentException("Origem inválida!");
		}
		if (destino.isBlank() || destino.isEmpty()) {
			throw new IllegalArgumentException("Destino inválido!");
		}
		if (aviao == null || !avioes.contains(aviao)) {
			throw new IllegalArgumentException("È preciso selecionar um aviao!");
		}
		if (precoPrimeiraClasse < 0) {
			throw new IllegalArgumentException("Preço da primeira classe inválido!");
		}
		if (precoClasseEconomica < 0) {
			throw new IllegalArgumentException("Preço da classe econômica inválido!");
		}
		return new Voo(data, hora, origem, destino, precoPrimeiraClasse, precoClasseEconomica);
	}
	
	public static void criaVoo(Voo voo, Aviao aviao) {
		voo.setAviao(avioes.get(avioes.indexOf(aviao)));
		log.registrarAcao("Novo voo criado: " + voo);
	}
	
	public static void mostraAviso(String message, int type) {
		switch (type) {
			case JOptionPane.ERROR_MESSAGE:
				JOptionPane.showMessageDialog(null, message, "Erro", type);
				break;
			case JOptionPane.PLAIN_MESSAGE:
				JOptionPane.showMessageDialog(null, message, "Sucesso", type);
				break;
			default:
				JOptionPane.showMessageDialog(null, message, "Aviso", JOptionPane.INFORMATION_MESSAGE);
				break;
		}
	}

	public static Usuario getUsuarioAtual() {
		return usuarioAtual;
	}

	public static void setUsuarioAtual(Usuario usuarioAtual) {
		Sistema.usuarioAtual = usuarioAtual;
	}
}
