package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		
		String caminhoOrigem = "D:" + File.separator + "ws-eclipse" + File.separator + "Pastas" + File.separator
				+ "ConteudosTXT" + File.separator + "servidor_log.txt";
		
		String caminhoDestino = "D:" + File.separator + "ws-eclipse" + File.separator + "Pastas" + File.separator
				+ "ConteudosTXT" + File.separator + "relatorio_erros.txt";

		File arquivoOrigem = new File(caminhoOrigem);
		File arquivoDestino = new File(caminhoDestino);


		File pasta = arquivoDestino.getParentFile();
		if (pasta != null && !pasta.exists()) {
			pasta.mkdirs();
			System.out.println("Diretório de destino verificado/criado.");
		}

		System.out.println("Iniciando a filtragem de logs...");

		try (Scanner leitor = new Scanner(arquivoOrigem);
			 FileWriter fw = new FileWriter(arquivoDestino, true); 
			 BufferedWriter escritor = new BufferedWriter(fw)) {

			escritor.write("--- NOVA ANÁLISE DE LOGS EM: 2026-11-09 ---");
			escritor.newLine();


			while (leitor.hasNextLine()) {
				String linha = leitor.nextLine();

				if (linha.contains("[ERROR]")) {
					escritor.write(linha); 
					escritor.newLine();  
				}
			}
			
			System.out.println("Análise concluída! Verifique o arquivo 'relatorio_erros.txt'.");

		} catch (IOException erro) {
			System.out.println("Erro crítico ao processar os arquivos: " + erro.getMessage());
		}

	}

}
