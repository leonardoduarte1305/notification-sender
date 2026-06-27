package br.dev.notificationsender.events.contratos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDTO {

    private String subject;

    private String message;

    private List<String> to;

    private String attachmentsAbsolutePath;

    private String attachmentsNameToBeDisplayed;

}
