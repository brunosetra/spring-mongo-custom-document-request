import * as React from "react";
import { cn } from "@/lib/utils";
import { Loader2 } from "lucide-react";

interface LoadingSpinnerProps {
  size?: "sm" | "md" | "lg";
  color?: "primary" | "secondary" | "white";
  className?: string;
  text?: string;
  overlay?: boolean;
}

export const LoadingSpinner = React.forwardRef<
  HTMLDivElement,
  LoadingSpinnerProps
>(
  (
    { size = "md", color = "primary", className, text, overlay = false },
    ref,
  ) => {
    const sizeClasses = {
      sm: "h-4 w-4",
      md: "h-8 w-8",
      lg: "h-12 w-12",
    };

    const colorClasses = {
      primary: "text-primary",
      secondary: "text-secondary-foreground",
      white: "text-white",
    };

    const textSizeClasses = {
      sm: "text-sm",
      md: "text-base",
      lg: "text-lg",
    };

    if (overlay) {
      return (
        <div
          ref={ref}
          className={cn(
            "fixed inset-0 z-50 flex items-center justify-center bg-background/80 backdrop-blur-sm",
            className,
          )}
        >
          <div className="flex flex-col items-center gap-3">
            <Loader2
              className={cn(
                sizeClasses[size],
                colorClasses[color],
                "animate-spin",
              )}
            />
            {text && (
              <p className={cn(textSizeClasses[size], "text-muted-foreground")}>
                {text}
              </p>
            )}
          </div>
        </div>
      );
    }

    return (
      <div
        ref={ref}
        className={cn("flex items-center justify-center gap-3", className)}
      >
        <Loader2
          className={cn(sizeClasses[size], colorClasses[color], "animate-spin")}
        />
        {text && (
          <p className={cn(textSizeClasses[size], "text-muted-foreground")}>
            {text}
          </p>
        )}
      </div>
    );
  },
);

LoadingSpinner.displayName = "LoadingSpinner";
