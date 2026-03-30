import * as React from "react";
import { cn } from "@/lib/utils";

interface IconWrapperProps {
  icon: React.ReactNode;
  size?: "sm" | "md" | "lg";
  color?: "primary" | "secondary" | "muted";
  className?: string;
  onClick?: () => void;
}

export const IconWrapper = React.forwardRef<HTMLDivElement, IconWrapperProps>(
  ({ icon, size = "md", color = "muted", className, onClick }, ref) => {
    const sizeClasses = {
      sm: "h-4 w-4",
      md: "h-5 w-5",
      lg: "h-6 w-6",
    };

    const colorClasses = {
      primary: "text-primary",
      secondary: "text-secondary-foreground",
      muted: "text-muted-foreground",
    };

    return (
      <div
        ref={ref}
        className={cn(
          "flex items-center justify-center",
          sizeClasses[size],
          colorClasses[color],
          onClick && "cursor-pointer hover:opacity-80 transition-opacity",
          className,
        )}
        onClick={onClick}
      >
        {icon}
      </div>
    );
  },
);

IconWrapper.displayName = "IconWrapper";
