import * as React from "react";
import { cn } from "@/lib/utils";

interface DividerProps {
  orientation?: "horizontal" | "vertical";
  className?: string;
}

export const Divider = React.forwardRef<HTMLHRElement, DividerProps>(
  ({ orientation = "horizontal", className }, ref) => {
    return (
      <hr
        ref={ref}
        className={cn(
          "shrink-0 bg-border",
          orientation === "horizontal" ? "w-full" : "h-full w-px",
          className,
        )}
      />
    );
  },
);

Divider.displayName = "Divider";
