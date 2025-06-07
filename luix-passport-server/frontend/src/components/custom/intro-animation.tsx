import React, { ReactNode } from "react";
import { motion } from "framer-motion";

// Component props interface
interface IntroComponentProps {
  children: ReactNode;
  /**
   * Animation delay in seconds
   * @default 0.1
   */
  delay?: number;
  /**
   * Custom CSS class name
   */
  className?: string;
  /**
   * Custom style object
   */
  style?: React.CSSProperties;
  /**
   * z-index offset (counts down from 50)
   * @default 0
   */
  zIndexOffset?: number;
}

// Animation configuration constants
const animationConfig = {
  duration: 0.4, // Animation duration in seconds
  ease: [0.42, 0, 0.58, 1] as const, // Easing function (ease-in-out)
  initialDelay: 0.33333 // Base delay before animation starts
};

/**
 * Component that animates in from the right
 * @param children - Content to animate
 * @param delay - Delay multiplier (added to initialDelay)
 * @param className - Optional CSS class
 * @param style - Optional style object
 * @param zIndexOffset - Controls z-index (50 - offset)
 */
export const IntroX: React.FC<IntroComponentProps> = ({
                                                        children,
                                                        delay = 0.1,
                                                        className,
                                                        style,
                                                        zIndexOffset = 0
                                                      }) => (
  <motion.div
    // Initial hidden state (translated right and transparent)
    initial={{ opacity: 0, x: 50 }}
    // Animated visible state
    animate={{
      opacity: 1,
      x: 0,
      transition: {
        duration: animationConfig.duration,
        ease: animationConfig.ease,
        delay: animationConfig.initialDelay + delay
      }
    }}
    className={className}
    style={{
      position: 'relative',
      zIndex: 50 - zIndexOffset, // Calculated z-index
      ...style // Merged with custom styles
    }}
  >
    {children}
  </motion.div>
);

/**
 * Component that animates in from the left
 * @param children - Content to animate
 * @param delay - Delay multiplier (added to initialDelay)
 * @param className - Optional CSS class
 * @param style - Optional style object
 * @param zIndexOffset - Controls z-index (50 - offset)
 */
export const MinusIntroX: React.FC<IntroComponentProps> = ({
                                                             children,
                                                             delay = 0.1,
                                                             className,
                                                             style,
                                                             zIndexOffset = 0
                                                           }) => (
  <motion.div
    initial={{ opacity: 0, x: -50 }}
    animate={{
      opacity: 1,
      x: 0,
      transition: {
        duration: animationConfig.duration,
        ease: animationConfig.ease,
        delay: animationConfig.initialDelay + delay
      }
    }}
    className={className}
    style={{
      position: 'relative',
      zIndex: 50 - zIndexOffset,
      ...style
    }}
  >
    {children}
  </motion.div>
);

/**
 * Component that animates in from the bottom
 * @param children - Content to animate
 * @param delay - Delay multiplier (added to initialDelay)
 * @param className - Optional CSS class
 * @param style - Optional style object
 * @param zIndexOffset - Controls z-index (50 - offset)
 */
export const IntroY: React.FC<IntroComponentProps> = ({
                                                        children,
                                                        delay = 0.1,
                                                        className,
                                                        style,
                                                        zIndexOffset = 0
                                                      }) => (
  <motion.div
    initial={{ opacity: 0, y: 50 }}
    animate={{
      opacity: 1,
      y: 0,
      transition: {
        duration: animationConfig.duration,
        ease: animationConfig.ease,
        delay: animationConfig.initialDelay + delay
      }
    }}
    className={className}
    style={{
      position: 'relative',
      zIndex: 50 - zIndexOffset,
      ...style
    }}
  >
    {children}
  </motion.div>
);

/**
 * Component that animates in from the top
 * @param children - Content to animate
 * @param delay - Delay multiplier (added to initialDelay)
 * @param className - Optional CSS class
 * @param style - Optional style object
 * @param zIndexOffset - Controls z-index (50 - offset)
 */
export const MinusIntroY: React.FC<IntroComponentProps> = ({
                                                             children,
                                                             delay = 0.1,
                                                             className,
                                                             style,
                                                             zIndexOffset = 0
                                                           }) => (
  <motion.div
    initial={{ opacity: 0, y: -50 }}
    animate={{
      opacity: 1,
      y: 0,
      transition: {
        duration: animationConfig.duration,
        ease: animationConfig.ease,
        delay: animationConfig.initialDelay + delay
      }
    }}
    className={className}
    style={{
      position: 'relative',
      zIndex: 50 - zIndexOffset,
      ...style
    }}
  >
    {children}
  </motion.div>
);