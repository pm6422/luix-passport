"use client";
import React from "react";
import { motion } from "motion/react";

export default function ColourfulText({ text }: { text: string }) {
  const colors = [
    "rgb(210, 248, 210)",  // #D2F8D2
    "rgb(160, 231, 160)",  // #A0E7A0
    "rgb(107, 203, 119)",  // #6BCB77
    "rgb(63, 163, 77)",    // #3FA34D
    "rgb(45, 106, 79)",    // #2D6A4F
    "rgb(210, 248, 210)",  // #D2F8D2
    "rgb(160, 231, 160)",  // #A0E7A0
    "rgb(107, 203, 119)",  // #6BCB77
    "rgb(63, 163, 77)",    // #3FA34D
    "rgb(45, 106, 79)"     // #2D6A4F
  ];

  const [currentColors, setCurrentColors] = React.useState(colors);
  const [count, setCount] = React.useState(0);

  React.useEffect(() => {
    const interval = setInterval(() => {
      const shuffled = [...colors].sort(() => Math.random() - 0.5);
      setCurrentColors(shuffled);
      setCount((prev) => prev + 1);
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  return text.split("").map((char, index) => (
    <motion.span
      key={`${char}-${count}-${index}`}
      initial={{
        y: 0,
      }}
      animate={{
        color: currentColors[index % currentColors.length],
        y: [0, -3, 0],
        scale: [1, 1.01, 1],
        filter: ["blur(0px)", `blur(5px)`, "blur(0px)"],
        opacity: [1, 0.8, 1],
      }}
      transition={{
        duration: 0.5,
        delay: index * 0.05,
      }}
      className="inline-block whitespace-pre font-sans tracking-tight"
    >
      {char}
    </motion.span>
  ));
}
