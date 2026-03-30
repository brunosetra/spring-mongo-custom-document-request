import * as React from "react";
import { cn } from "@/lib/utils";

interface ResponsiveContainerProps {
  children: React.ReactNode;
  className?: string;
}

export const ResponsiveContainer = React.forwardRef<
  HTMLDivElement,
  ResponsiveContainerProps
>(({ children, className }, ref) => {
  return (
    <div ref={ref} className={cn("w-full", className)}>
      {children}
    </div>
  );
});

ResponsiveContainer.displayName = "ResponsiveContainer";
