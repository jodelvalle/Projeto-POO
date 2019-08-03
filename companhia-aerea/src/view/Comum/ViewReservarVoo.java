package view.Comum;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import principal.Sistema;
import usuario.Atendente;
import view.MenuAtendente.ViewMenuAtendente;
import view.MenuPassageiro.ViewMenuPassageiro;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ViewReservarVoo extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel panel;
	private JButton btnSair;
	private JButton btnReservarVoo;

	public ViewReservarVoo() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				sair();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		panel = new JPanel();
		panel.setBounds(12, 209, 426, 49);
		contentPane.add(panel);
		panel.setLayout(new GridLayout(0, 2, 20, 0));
		
		btnReservarVoo = new JButton("Reservar Voo");
		panel.add(btnReservarVoo);
		
		btnSair = new JButton("Sair");
		btnSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sair();
			}
		});
		panel.add(btnSair);
	}
	
	private void sair() {
		if (Sistema.getUsuarioAtual() instanceof Atendente) {
			new ViewMenuAtendente().setVisible(true);
		} else {
			new ViewMenuPassageiro().setVisible(true);
		}
		dispose();
	}
}
