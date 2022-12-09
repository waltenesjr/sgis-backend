package br.com.oi.sgis.util;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ReportUtils {

    private ReportUtils(){}

    public static byte[] exportPdf(JasperPrint report) throws JRException {
        JRPdfExporter exporter = new JRPdfExporter();
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        exporter.setExporterInput(new SimpleExporterInput(report));
        exporter.setExporterOutput(
                new SimpleOutputStreamExporterOutput(pdfOutputStream));

        SimplePdfReportConfiguration reportConfig
                = new SimplePdfReportConfiguration();
        reportConfig.setSizePageToContent(true);
        reportConfig.setForceLineBreakPolicy(false);

        exporter.exportReport();
        return pdfOutputStream.toByteArray();

    }

    public static JasperReport getJasperReport(String reportPath) throws JRException {
        InputStream reportMovement = ReportUtils.class.getResourceAsStream(reportPath);
        JasperDesign jasperDesign = JRXmlLoader.load(reportMovement);
        return JasperCompileManager.compileReport(jasperDesign);
    }
}
