import React, { ReactNode } from "react";
import { motion } from "framer-motion";

interface IntroComponentProps {
  children: ReactNode;
  /**
   * 动画延迟时间（秒）
   * @default 0.1
   */
  delay?: number;
  /**
   * 自定义类名
   */
  className?: string;
  /**
   * 自定义样式
   */
  style?: React.CSSProperties;
  /**
   * z-index 值（从50开始递减）
   * @default 0
   */
  zIndexOffset?: number;
}

// 动画配置
const animationConfig = {
  duration: 0.4,
  ease: [0.42, 0, 0.58, 1] as const,
  initialDelay: 0
};

/**
 * 从右侧进入的组件
 */
export const IntroX: React.FC<IntroComponentProps> = ({
                                                        children,
                                                        delay = 0.1,
                                                        className,
                                                        style,
                                                        zIndexOffset = 0
                                                      }) => (
  <motion.div
    initial={{ opacity: 0, x: 50 }}
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
 * 从左侧进入的组件
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
 * 从下方进入的组件
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
 * 从上方进入的组件
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