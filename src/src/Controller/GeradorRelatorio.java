package application;

import java.util.LinkedList;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;


public class GeradorRelatorio<ClasseDados> {
	private String diretorioDoRelatorio;
	private String diretorioDoJRXML;
	private String nomeRelatorio;
	private String relatorio;
	private JasperReport report;
	private JasperPrint print;
	
	public GeradorRelatorio(String relatorio) {
            this.diretorioDoJRXML = this.getClass().getClassLoader().getResource("").getPath();
            this.diretorioDoRelatorio = this.diretorioDoJRXML;
            this.relatorio = relatorio;		
	}

	public void setNomeRelatorio(String nomeRelatorio){
            this.nomeRelatorio = nomeRelatorio;
	}
	
	public String getNomeRelatorio(){
            return this.nomeRelatorio;		
	}
	
	public void setDiretorioDoRelatorio(String diretorio){
            this.diretorioDoRelatorio = diretorio;
	}
	
	public String getDiretorioDoRelatorio(){
            return this.diretorioDoRelatorio;
	}
		
	public void gerarRelatorio(LinkedList<ClasseDados> dados) throws Exception{
            this.report = JasperCompileManager.compileReport(this.diretorioDoJRXML + "application/" + this.relatorio + ".jrxml");
            this.print = JasperFillManager.fillReport(report, null, new JRBeanCollectionDataSource(dados));
	}
	
	public void exportarParaPdf() throws Exception{
            JasperExportManager.exportReportToPdfFile(print, this.diretorioDoRelatorio + "/" + this.nomeRelatorio);		
	}
	
	public void imprimir() throws Exception{		
            JasperPrintManager.printReport(print, true);		
	}
}
