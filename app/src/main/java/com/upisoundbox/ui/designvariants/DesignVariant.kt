package com.upisoundbox.ui.designvariants

enum class DesignVariant(
    val id: String,
    val number: Int,
    val title: String,
    val subtitle: String
) {
    DESIGN_01("design_01", 1, "Design 01: Premium Banking", "Refined light surfaces, deep emerald, strong typography"),
    DESIGN_02("design_02", 2, "Design 02: Minimal Merchant", "Ultra-clean single-glance console, low visual noise"),
    DESIGN_03("design_03", 3, "Design 03: Editorial Financial", "Typography-first, receipt slip cards, generous whitespace"),
    DESIGN_04("design_04", 4, "Design 04: Dense Operations", "High-density metrics, split dashboard grid, command dock"),
    DESIGN_05("design_05", 5, "Design 05: Modern Material 3", "Tonal containers, expressive pills, dynamic floating bar"),
    DESIGN_06("design_06", 6, "Design 06: Soft Premium", "Warm ivory stone, soft curved cards, calm visual rhythm"),
    DESIGN_07("design_07", 7, "Design 07: Monochrome Pro", "Swiss monochrome, high contrast, clean tabular ledger"),
    DESIGN_08("design_08", 8, "Design 08: Large-Type Merchant", "Extra-large numerals, 5-meter glanceability, bold tactile controls"),
    DESIGN_09("design_09", 9, "Design 09: Compact Tool", "Modular industrial borders, telemetry data rows, tabular layout"),
    DESIGN_10("design_10", 10, "Design 10: Neo-Sleek Glass", "Frosted glass overlay cards, asymmetric geometry, floating dock")
}
