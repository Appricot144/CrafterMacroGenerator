package com.appricot.feature.crafterMacroGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.appricot.feature.crafterMacroGenerator.entity.CraftingAction;
import com.appricot.feature.crafterMacroGenerator.skills.BasicSynthesis;

@Service
public class SkillService {

    private final Map<String, CraftingAction> allSkills = new HashMap<>();
    private final Map<String, Integer> skillLevelRequirements = new HashMap<>();

    @PostConstruct
    public void initialize() {
        // すべてのスキルを登録
        registerSkills();
    }

    // TODO impl various skills
    private void registerSkills() {
        // 基本作業系スキル
        registerSkill(new BasicSynthesis(), 1);
//        registerSkill(new CarefulSynthesis(), 15);
//        registerSkill(new Groundwork(), 42);
//        registerSkill(new IntensiveSynthesis(), 58);
//        registerSkill(new PrudentSynthesis(), 66);
//        
        // 基本加工系スキル
//        registerSkill(new BasicTouch(), 5);
//        registerSkill(new StandardTouch(), 18);
//        registerSkill(new ByregotsBlessing(), 50);
//        registerSkill(new PreciseTouch(), 53);
//        registerSkill(new PrudentTouch(), 64);
//        
        // バフ系スキル
//        registerSkill(new InnerQuiet(), 11);
//        registerSkill(new WasteNot(), 15);
//        registerSkill(new Veneration(), 21);
//        registerSkill(new GreatStrides(), 21);
//        registerSkill(new Innovation(), 26);
//        registerSkill(new WasteNotII(), 47);
//        registerSkill(new MuscleMemory(), 54);
//        registerSkill(new Manipulation(), 58);
//        
        // 特殊スキル
//        registerSkill(new MastersMend(), 7);
//        registerSkill(new TricksOfTheTrade(), 13);
//        registerSkill(new Observe(), 13);
//        registerSkill(new Reflect(), 69);
//        registerSkill(new PreparatoryTouch(), 71);
//        registerSkill(new DelicateSynthesis(), 76);
//        registerSkill(new TrainedEye(), 80);
    }

    private void registerSkill(CraftingAction skill, int levelRequirement) {
        allSkills.put(skill.getName(), skill);
        skillLevelRequirements.put(skill.getName(), levelRequirement);
    }

    /**
     * すべてのスキルを取得
     */
    public List<CraftingAction> getAllSkills() {
        return new ArrayList<>(allSkills.values());
    }

    /**
     * 指定されたレベルで使用可能なスキルを取得
     */
    public List<CraftingAction> getSkillsByLevel(int level) {
        if (level <= 0) {
            return getAllSkills(); // レベル指定がない場合はすべて返す
        }
        
        return allSkills.entrySet().stream()
                .filter(entry -> skillLevelRequirements.get(entry.getKey()) <= level)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}