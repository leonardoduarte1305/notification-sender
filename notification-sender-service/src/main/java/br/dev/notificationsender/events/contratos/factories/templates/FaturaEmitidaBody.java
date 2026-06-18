package br.dev.notificationsender.events.contratos.factories.templates;

import br.dev.notificationsender.events.contratos.FaturaEmitidaEvent;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class FaturaEmitidaBody implements DadosEmail {

    private final FaturaEmitidaEvent evento;

    @Override
    public String getMessage() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <title>Nova Fatura Disponível</title>
                </head>
                <body style="font-family: Arial, Helvetica, sans-serif; background-color: #f5f7fa; padding: 20px; margin: 0;">
                
                <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; border: 1px solid #e5e7eb; overflow: hidden;">
                
                    <div style="background-color: #2d6cab; color: white; padding: 20px;">
                        <h2 style="margin: 0;">Nova Fatura Disponível</h2>
                    </div>
                
                    <div style="padding: 24px; color: #374151; line-height: 1.6;">
                        <p>Olá</p>
                
                        <p>
                            O síndico emitiu uma nova fatura para o apartamento <strong>%d</strong>.
                        </p>
                
                        <div style="background-color: #f9fafb; border-left: 4px solid #2d6cab; padding: 16px; margin: 20px 0;">
                            <p style="margin: 0 0 8px 0;">
                                <strong>Valor total:</strong> R$ %.2f
                            </p>
                
                            <p style="margin: 0;">
                                <strong>Vencimento:</strong> %s
                            </p>
                        </div>
                
                        <p>
                            Recomendamos realizar o pagamento até a data de vencimento para evitar eventuais encargos.
                        </p>
                
                        <p>
                            Atenciosamente,<br>
                            <strong>Condomínio Organizado</strong>
                        </p>
                    </div>
                
                </div>
                
                </body>
                </html>
                """.formatted(evento.numeroApartamento(), evento.valorTotal(), evento.dataVencimento().format(dateFormat));
    }

    @Override
    public String getSubject() {
        return "Você tem uma nova fatura!";
    }

}
