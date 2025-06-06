"use client";
import React from "react";
import { motion } from "motion/react";

export default function ColourfulText({ text }: { text: string }) {
  const colors = [
    "rgb(224, 187, 228)",  // #E0BBE4
    "rgb(200, 158, 196)",  // #C89EC4
    "rgb(171, 114, 176)",  // #AB72B0
    "rgb(142, 68, 173)",   // #8E44AD
    "rgb(108, 52, 131)",   // #6C3483
    "rgb(224, 187, 228)",  // #E0BBE4
    "rgb(200, 158, 196)",  // #C89EC4
    "rgb(171, 114, 176)",  // #AB72B0
    "rgb(142, 68, 173)",   // #8E44AD
    "rgb(108, 52, 131)"    // #6C3483
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
