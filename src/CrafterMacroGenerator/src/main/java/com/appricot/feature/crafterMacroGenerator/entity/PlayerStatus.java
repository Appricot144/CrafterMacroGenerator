package com.appricot.feature.crafterMacroGenerator.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStatus {
    private int craftingLevel;
    private int craftsmanship;
    private int control;
    private int cp;
}
