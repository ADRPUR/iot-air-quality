export function presetToRange(preset) {
    const now = Date.now();
    switch (preset) {
        case "1h":  return { fromMs: now - 1 * 60 * 60 * 1000 };
        case "6h":  return { fromMs: now - 6 * 60 * 60 * 1000 };
        case "24h": return { fromMs: now - 24 * 60 * 60 * 1000 };
        default:    return {};
    }
}