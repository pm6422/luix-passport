import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs));
}

export function calculateTrend(current: number, previous: number) {
    if (!previous || previous === 0) {
        return { trend: "0%", isUp: true };
    }
    const diff = current - previous;
    const percentage = (Math.abs(diff) / previous) * 100;
    return {
        trend: `${diff >= 0 ? "+" : "-"}${percentage.toFixed(1)}%`,
        isUp: diff >= 0,
    };
}
