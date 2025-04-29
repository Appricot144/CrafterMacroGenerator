package com.appricot.feature.crafterMacroGenerator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class CraftingAction {
    private String name;
    private int cpCost;
    private ActionType type;
    private int durabilityCost;
    private int executeTime;

    public abstract CraftingState apply(CraftingState currentState);
    public abstract boolean canExecute(CraftingState currentState);
}
