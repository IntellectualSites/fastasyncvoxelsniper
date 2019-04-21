/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.perform;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.TreeMap;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

/**
 * @author Voxel
 */

/* The m/i/c system of naming performers: <placement-option>[replacement-option][extras]
 *
 * placement-option is mandatory and can be material(m) [for /v], ink(i) [for /vi] or combo(c) [for both]
 * replacement-option is optional and can be m [for /vr], i [for /vir] or c [for both]
 * extras is optional and can be update(u) [for graphical glitch], physics(p) [for no-phys] or up [for both]
 *
 * new extra: n = no undo
 *
 * The main benefit of this system is that it provides the least possible number of characters in the paramaters
 * while guaranteeing that all sensible combinations will be made.  Additionally, the names will be VERY consistent
 *
 * EX Old System: /b b isrcup (use /v, /vi, /vr and /vir, update graphics and no physics)
 * EX New System: /b b ccup   (two characters shorter, good because snipers have been complaing about keystrokes)
 *
 */

/* This enum is getting REALLY Long, would it be possible to algorithmically generate the full performer
 * from the pieces? So if the performer name is of the for m*, you'll setTypeId whereas if it is of the
 * form c* you'd setTypeIdandData?  Similarly, if the performer is of the form *p, any setTypeId's or setTypeIdandData's
 * will be set to false instead of true? The middle bits might be tougher, being of the form _m* perhaps?
 * Regex to the rescue, am I right? - Giltwist
 */

public enum Performers {

	MATERIAL(MaterialPerformer.class, "m", "material"),
	MATERIAL_NOPHYS(MaterialNoPhysPerformer.class, "mp", "mat-nophys"),
	MAT_MAT(MatMatPerformer.class, "mm", "mat-mat"),
	MAT_MAT_NOPHYS(MatMatNoPhysPerformer.class, "mmp", "mat-mat-nophys"),
	MAT_INK(MatInkPerformer.class, "mi", "mat-ink"),
	MAT_INK_NOPHYS(MatInkNoPhysPerformer.class, "mip", "mat-ink-nophys"),
	MAT_COMBO(MatComboPerformer.class, "mc", "mat-combo"),
	MAT_COMBO_NOPHYS(MatComboNophysPerformer.class, "mcp", "mat-combo-nophys"),
	INK(InkPerformer.class, "i", "ink"),
	INK_MAT(InkMatPerformer.class, "im", "ink-mat"),
	INK_INK(InkInkPerformer.class, "ii", "ink-ink"),
	INK_COMBO(InkComboPerformer.class, "ic", "ink-combo"),
	INK_INK_NOPHYS(InkInkNoPhysPerformer.class, "iip", "ink-ink-nophys"),
	INK_NOPHYS(InkNoPhysPerformer.class, "ip", "ink-nophys"),
	INK_MAT_NOPHYS(InkMatNoPhysPerformer.class, "imp", "ink-mat-nophys"),
	INK_COMBO_NOPHYS(InkComboNoPhysPerformer.class, "icp", "ink-combo-nophys"),
	COMBO(ComboPerformer.class, "c", "combo"),
	COMBO_NOPHYS(ComboNoPhysPerformer.class, "cp", "combo-nophys"),
	COMBO_MAT(ComboMatPerformer.class, "cm", "combo-mat"),
	COMBO_MAT_NOPHYS(ComboMatNoPhysPerformer.class, "cmp", "combo-mat-nophys"),
	COMBO_INK(ComboInkPerformer.class, "ci", "combo-ink"),
	COMBO_INK_NOPHYS(ComboInkNoPhysPerformer.class, "cip", "combo-ink-nophys"),
	COMBO_COMBO(ComboComboPerformer.class, "cc", "combo-combo"),
	COMBO_COMBO_NOPHYS(ComboComboNoPhysPerformer.class, "ccp", "combo-combo-nophys"),
	EXCLUDE_MATERIAL(ExcludeMatPerformer.class, "xm", "exclude-mat"),
	EXCLUDE_INK(ExcludeInkPerformer.class, "xi", "exclude-ink"),
	EXCLUDE_COMBO(ExcludeComboPerformer.class, "xc", "exclude-combo"),
	INCLUDE_MATERIAL(IncludeMatPerformer.class, "nm", "include-mat"),
	INCLUDE_INK(IncludeInkPerformer.class, "ni", "include-ink"),
	INCLUDE_COMBO(IncludeComboPerformer.class, "nc", "include-combo"),
	MAT_NO_UNDO(NoUndoPerformer.class, "noundo", "noundo");
	//Other Performers which don't exist yet but are required for a full set of possibilities that actually could potentially do something:
	//List does not include any no-physics, unless materials are being placed (or combo), or any update unless ink is being placed (or combo) -Gavjenks
	//MAT_MAT_UPDATE(           pMatMatUpdate.class,            "mmu",          "mat-mat-update"    ),      //              place mat, replace mat, graphical update
	//MAT_COMBO_UPDATE(         pMatComboUpdate.class,          "mcu",          "mat-combo-update"  ),      //              place mat, replace combo, graphical update
	//MAT_COMBO_NOPHYS_UPDATE(  pMatComboNoPhysUpdate.class,    "mcup",         "mat-combo-update-nophys"), //              place mat, replace combo, update, no physics
	//MAT_INK_UPDATE(           pMatInkUpdate.class,            "miu",          "mat-ink-update"),          //              place mat, replace ink, graphical update
	//MAT_INK_NOPHYS_UPDATE(    pMatInkNoPhysUpdate.class,      "miup",         "mat-ink-update-nophys"),   //              place mat, replace ink, graphical update no physics
	//INK_MAT_UPDATE(           pInkMatUpdate.class,            "imu",          "ink-mat-update"),          //              place ink, replace mat, graphical update
	//INK_INK_UPDATE(           pInkInkUpdate.class,            "iiu",          "ink-ink-update"),          //              place ink, replace ink, graphical update
	//INK_COMBO_UPDATE(         pInkComboUpdate.class,          "icu",          "ink-combo-update"),        //              place ink, replace combo, graphical update
	//COMBO_MAT_UPDATE(         pComboMatUpdate.class,          "cmu",          "combo-mat-update"),        //              place combo, replace mat, graphical update
	//COMBO_MAT_NOPHYS_UPDATE(  pComboMatNoPhysUpdate.class,    "cmup",         "combo-mat-update-nophys"), //              place combo, replace mat, graphical update, no physics
	//COMBO_INK_UPDATE(         pComboInkUpdate.class,          "ciu",          "combo-ink-update"),        //              place combo, replace ink, graphical update
	//COMBO_INK_NOPHYS_UPDATE(  pComboInkNoPhysUpdate.class,    "ciup",         "combo-ink-update-nophys"), //              place combo, replace ink, graphical update, no physics
	//COMBO_COMBO_UPDATE(       pComboComboUpdate.class,        "ccu",          "combo-combo-update"),      //              place combo, replace combo, graphical update
	//COMBO_COMBO_NOPHYS_UPDATE(pComboComboNoPhysUpdate.class,  "ccup",         "combo-combo-update-nophys"),//             place combo, replace combo, graphical update, no physics

	private static final Map<String, Performer> PERFORMERS;
	private static final Map<String, String> LONG_NAMES;
	private Class<? extends Performer> performerClass;
	private String shortName;
	private String longName;
	private static String performerListShort = "";
	private static String performerListLong = "";

	Performers(Class<? extends Performer> performerClass, String shortName, String longName) {
		this.performerClass = performerClass;
		this.shortName = shortName;
		this.longName = longName;
	}

	public static String getPerformerListShort() {
		return performerListShort;
	}

	public static void setPerformerListShort(String performerListShort) {
		Performers.performerListShort = performerListShort;
	}

	public static String getPerformerListLong() {
		return performerListLong;
	}

	public static void setPerformerListLong(String performerListLong) {
		Performers.performerListLong = performerListLong;
	}

	@Nullable
	private Performer getPerformer() {
		try {
			Constructor<? extends Performer> constructor = this.performerClass.getConstructor();
			return constructor.newInstance();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			exception.printStackTrace();
		}
		return null;
	}

	public static Performer getPerformer(String s) {
		return PERFORMERS.containsKey(s) ? PERFORMERS.get(s) : PERFORMERS.get(LONG_NAMES.get(s));
	}

	public static boolean has(String s) {
		return PERFORMERS.containsKey(s);
	}

	static {
		PERFORMERS = new TreeMap<>();
		LONG_NAMES = new TreeMap<>();
		for (Performers performer : values()) {
			PERFORMERS.put(performer.shortName, performer.getPerformer());
			LONG_NAMES.put(performer.longName, performer.shortName);
			performerListShort = performerListShort + ChatColor.GREEN + performer.shortName + ChatColor.RED + ", ";
			performerListLong = performerListLong + ChatColor.GREEN + performer.longName + ChatColor.RED + ", ";
		}
		performerListShort = performerListShort.substring(0, performerListShort.length() - 2);
		performerListLong = performerListLong.substring(0, performerListLong.length() - 2);
	}
}
