import * as React from "react";
import { cn } from "@/lib/utils";

interface LayoutWrapperProps {
  children: React.ReactNode;
  className?: string;
  size?: "sm" | "md" | "lg";
}

export const LayoutWrapper = React.forwardRef<
  HTMLDivElement,
  LayoutWrapperProps
>(({ children, className, size = "md" }, ref) => {
  const sizeClasses = {
    sm: "max-w-4xl",
    md: "max-w-6xl",
    lg: "max-w-7xl",
  };

  return (
    <div
      ref={ref}
      className={cn(
        "mx-auto px-4 sm:px-6 lg:px-8",
        sizeClasses[size],
        className,
      )}
    >
      {children}
    </div>
  );
});

LayoutWrapper.displayName = "LayoutWrapper";
