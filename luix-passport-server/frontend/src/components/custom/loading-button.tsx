import * as React from "react";
import { Slot, Slottable } from "@radix-ui/react-slot";
import { cva, type VariantProps } from "class-variance-authority";
import { cn } from "@/lib/utils";
import { motion } from "framer-motion";

const buttonVariants = cva(
  "inline-flex items-center justify-center rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50",
  {
    variants: {
      variant: {
        default: "bg-primary text-primary-foreground hover:bg-primary/90",
        destructive: "bg-destructive text-destructive-foreground hover:bg-destructive/90",
        outline: "border border-input bg-background hover:bg-accent hover:text-accent-foreground",
        secondary: "bg-secondary text-secondary-foreground hover:bg-secondary/80",
        ghost: "hover:bg-accent hover:text-accent-foreground",
        link: "text-primary underline-offset-4 hover:underline",
      },
      size: {
        default: "h-10 px-4 py-2",
        sm: "h-9 rounded-md px-3",
        lg: "h-11 rounded-md px-8",
        icon: "h-10 w-10",
      },
    },
    defaultVariants: {
      variant: "default",
      size: "default",
    },
  },
);

export interface ButtonProps
  extends React.ButtonHTMLAttributes<HTMLButtonElement>,
    VariantProps<typeof buttonVariants> {
  asChild?: boolean;
  loading?: boolean;
  disabled?: boolean;
}

const LoadingDots = () => {
  return (
    <span className="relative ml-1 flex items-end justify-center h-4 mb-1">
      {[0, 1, 2].map((i) => (
        <motion.span
          key={i}
          className="mx-[1px] h-1.5 w-1.5 rounded-full bg-current"
          animate={{ scale: [1, 1.5, 1], opacity: [1, 0.7, 1] }}
          transition={{
            duration: 0.6,
            repeat: Infinity,
            ease: "easeInOut",
            delay: i * 0.15,
          }}
        />
      ))}
    </span>
  );
};

const LoadingButton = React.forwardRef<HTMLButtonElement, ButtonProps>(
  (
    { className, loading = false, children, disabled = false, variant, size, asChild = false, ...props },
    ref,
  ) => {
    const isDisabled = loading || disabled;
    const Comp = asChild ? Slot : "button";
    return (
      <Comp
        className={cn(buttonVariants({ variant, size, className }), "relative")}
        ref={ref}
        disabled={isDisabled}
        {...props}
      >
        <div className="flex items-end">
          <Slottable>{children}</Slottable>
          {loading && <LoadingDots />}
        </div>
      </Comp>
    );
  },
);
LoadingButton.displayName = "LoadingButton";

export { LoadingButton, buttonVariants };
