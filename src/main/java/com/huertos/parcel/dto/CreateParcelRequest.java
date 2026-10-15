package com.huertos.parcel.dto;

import com.huertos.parcel.util.InputSanitizer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateParcelRequest(

        @NotBlank(message = "El nombre de la parcela es obligatorio.")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres.")
        @Pattern(regexp = "^[\\p{L}\\p{N}][\\p{L}\\p{N} .,'()_-]*$",
                 message = "El nombre solo puede contener letras, números, espacios y los símbolos . , ' ( ) _ -")
        String name,

        @NotNull(message = "La ubicación (coordenadas GPS) es obligatoria.")
        @Valid
        LocationRequest location,

        @NotBlank(message = "El tipo de suelo es obligatorio.")
        @Pattern(regexp = "^(?i)(arcilloso|limoso|arenoso)$",
                 message = "Tipo de suelo inválido. Valores permitidos: arcilloso, limoso, arenoso.")
        String soilType,

        @NotBlank(message = "El responsable o líder de la parcela es obligatorio.")
        @Size(min = 2, max = 100, message = "El responsable debe tener entre 2 y 100 caracteres.")
        @Pattern(regexp = "^\\p{L}[\\p{L} .'-]*$",
                 message = "El responsable solo puede contener letras, espacios y los símbolos . ' -")
        String ownerOrLead
) {
    /** Constructor compacto: sanitiza antes de que se ejecuten las validaciones. */
    public CreateParcelRequest {
        name = InputSanitizer.sanitize(name);
        soilType = InputSanitizer.sanitize(soilType);
        ownerOrLead = InputSanitizer.sanitize(ownerOrLead);
    }
}
