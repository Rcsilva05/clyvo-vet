package br.com.fiap.solin.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TutorRequest(

        @NotBlank(message = "O nome do tutor e obrigatorio")
        @Size(min = 3, max = 120, message = "O nome deve ter entre 3 e 120 caracteres")
        String nome,

        @NotBlank(message = "O email e obrigatorio")
        @Email(message = "Email em formato invalido")
        @Size(max = 150)
        String email,

        @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$",
                message = "Telefone em formato invalido. Ex: (11) 91234-5678")
        String telefone
) {}
