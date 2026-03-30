import * as React from "react";
import { cn } from "@/lib/utils";
import { Card, CardContent } from "@/components/ui/card";
import { TrendingUp, TrendingDown, Minus } from "lucide-react";

export interface StatCardProps {
  title: string;
  value: string | number;
  trend?: {
    value: string;
    type: "up" | "down" | "neutral";
    label?: string;
  };
  icon?: React.ReactNode;
  variant?:
    | "default"
    | "primary"
    | "secondary"
    | "success"
    | "warning"
    | "error";
  className?: string;
}

export const StatCard = React.forwardRef<HTMLDivElement, StatCardProps>(
  ({ title, value, trend, icon, variant = "default", className }, ref) => {
    const getTrendIcon = () => {
      if (!trend) return null;
      switch (trend.type) {
        case "up":
          return <TrendingUp className="h-4 w-4" />;
        case "down":
          return <TrendingDown className="h-4 w-4" />;
        default:
          return <Minus className="h-4 w-4" />;
      }
    };

    const getTrendColor = () => {
      if (!trend) return "text-muted-foreground";
      switch (trend.type) {
        case "up":
          return "text-green-600";
        case "down":
          return "text-red-600";
        default:
          return "text-muted-foreground";
      }
    };

    const getVariantColor = () => {
      switch (variant) {
        case "primary":
          return "text-primary";
        case "secondary":
          return "text-secondary-foreground";
        case "success":
          return "text-green-600";
        case "warning":
          return "text-orange-600";
        case "error":
          return "text-red-600";
        default:
          return "text-primary";
      }
    };

    return (
      <Card
        ref={ref}
        className={cn("transition-all hover:shadow-lg", className)}
      >
        <CardContent className="p-6">
          <div className="flex items-start justify-between">
            <div className="flex-1">
              <h3 className="text-sm font-medium text-muted-foreground mb-2">
                {title}
              </h3>
              <div className="flex items-baseline gap-3">
                <span className="text-3xl font-bold">{value}</span>
                {trend && (
                  <div className="flex items-center gap-1">
                    {getTrendIcon()}
                    <span
                      className={cn("text-sm font-medium", getTrendColor())}
                    >
                      {trend.value}
                    </span>
                    {trend.label && (
                      <span className="text-sm text-muted-foreground">
                        {trend.label}
                      </span>
                    )}
                  </div>
                )}
              </div>
            </div>
            {icon && (
              <div
                className={cn(
                  "p-3 rounded-lg",
                  variant === "primary" && "bg-primary/10 text-primary",
                  variant === "secondary" &&
                    "bg-secondary/10 text-secondary-foreground",
                  variant === "success" && "bg-green-100 text-green-600",
                  variant === "warning" && "bg-orange-100 text-orange-600",
                  variant === "error" && "bg-red-100 text-red-600",
                )}
              >
                {icon}
              </div>
            )}
          </div>
        </CardContent>
      </Card>
    );
  },
);

StatCard.displayName = "StatCard";
