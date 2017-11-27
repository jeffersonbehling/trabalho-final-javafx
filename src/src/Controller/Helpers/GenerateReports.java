/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.Controller.Helpers;

import java.util.LinkedList;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author jefferson
 */
public class GenerateReports<ClassData> {
    private String dirReport;
	private String dirJRXML;
	private String reportName;
	private String report;
	private JasperReport jasperReport;
	private JasperPrint print;
	
	public GenerateReports(String report) {
		this.dirJRXML = this.getClass().getClassLoader().getResource("").getPath();
		this.dirReport = this.dirJRXML;
		this.report = report;		
	}

	public void setReportName(String reportName){
		this.reportName = reportName;
	}
	
	public String getReportName(){
		return this.reportName;		
	}
	
	public void setDirReport(String dir){
		this.dirReport = dir;
	}
	
	public String getDirReport(){
		return this.dirReport;
	}
		
	public void generateReport(LinkedList<ClassData> dados) throws Exception{
		this.jasperReport = JasperCompileManager.compileReport(this.dirJRXML + "src/View/Reports/" + this.report + ".jrxml");
		this.print = JasperFillManager.fillReport(report, null, new JRBeanCollectionDataSource(dados));
	}
	
	public void exportToPdf() throws Exception{
		JasperExportManager.exportReportToPdfFile(print, this.dirReport + "/" + this.reportName);		
	}
	
	public void print() throws Exception{		
		JasperPrintManager.printReport(print, true);		
	}
}
