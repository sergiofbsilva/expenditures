package module.workingCapital.scripts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import module.workingCapital.domain.WorkingCapital;
import module.workingCapital.domain.WorkingCapitalInitialization;
import module.workingCapital.domain.WorkingCapitalProcess;
import module.workingCapital.domain.WorkingCapitalSystem;
import module.workingCapital.domain.WorkingCapitalYear;
import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.scheduler.custom.CustomTask;
import pt.utl.ist.fenix.tools.util.excel.Spreadsheet;
import pt.utl.ist.fenix.tools.util.excel.Spreadsheet.Row;

public class DumpWorkingCapitalInformation extends CustomTask {

    @Override
    public void runTask() {
        final Spreadsheet spreadsheet = new Spreadsheet("FundosManeio");
        spreadsheet.setHeader("Ano");
        spreadsheet.setHeader("Unidade");
        spreadsheet.setHeader("Unidade Exploração");
        spreadsheet.setHeader("Data Criação Processo");
        spreadsheet.setHeader("Valor Anual Requerido");
        spreadsheet.setHeader("Valor Anual Máximo Autorizado");
        spreadsheet.setHeader("Valor em Caixa Atribuido");

        for (final VirtualHost virtualHost : MyOrg.getInstance().getVirtualHostsSet()) {
            if (virtualHost.getHostname().equals("dot.ist.utl.pt")) {
                final WorkingCapitalSystem workingCapitalSystem = virtualHost.getWorkingCapitalSystem();
                for (final WorkingCapitalYear workingCapitalYear : workingCapitalSystem.getWorkingCapitalYearsSet()) {
                    for (final WorkingCapital workingCapital : workingCapitalYear.getWorkingCapitalsSet()) {
                        final WorkingCapitalProcess workingCapitalProcess = workingCapital.getWorkingCapitalProcess();
                        final WorkingCapitalInitialization initialization = workingCapital.getWorkingCapitalInitialization();
                        if (initialization.isAuthorized()) {
                            final Row row = spreadsheet.addRow();

                            row.setCell(workingCapitalYear.getYear());
                            row.setCell(workingCapital.getUnit().getPresentationName());
                            row.setCell(workingCapital.getUnit().getAccountingUnit().getName());
                            row.setCell(workingCapitalProcess.getCreationDate().toString("yyyy-MM-dd HH:mm:ss"));
                            row.setCell(initialization.getRequestedAnualValue().toFormatString());
                            row.setCell(initialization.getMaxAuthorizedAnualValue().toFormatString());
                            row.setCell(initialization.getAuthorizedAnualValue().toFormatString());
                        }
                    }
                }
            }
        }

        outputXlsFile("FundosManeio.xls", spreadsheet);
    }

    private void outputXlsFile(String name, Spreadsheet sheet) {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            sheet.exportToXLSSheet(outputStream);
        } catch (IOException e) {
            taskLog("Something when wrong when exporting sheet:%s\n", e.getMessage());
        }
        output(name, outputStream.toByteArray());
    }

}
