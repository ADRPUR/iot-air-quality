const HOUR_MS = 60 * 60 * 1000;

export function presetToRange(preset, custom = {}) {
    const now = Date.now();

    switch (preset) {
        case "1h":
            return { preset, fromMs: now - 1 * HOUR_MS };          // open-ended
        case "6h":
            return { preset, fromMs: now - 6 * HOUR_MS };
        case "24h":
            return { preset, fromMs: now - 24 * HOUR_MS };
        case "custom":
            // custom = { fromMs, toMs } – both absolute, coming from the date-picker
            return { preset, ...custom };
        default:
            return {};                                             // empty range
    }
}

/** The distance between the ends (ms). If range is open-ended, calculates up to NOW. */
export function rangeSpanMs({ fromMs, toMs }) {
    if (fromMs == null) return 0;
    return (toMs ?? Date.now()) - fromMs;
}

/** Drag the range so that the right end = NOW, preserving the span. */
export function slideRangeToNow(range) {
    const span = rangeSpanMs(range);
    const toMs = Date.now();
    return { ...range, fromMs: toMs - span, toMs };
}
