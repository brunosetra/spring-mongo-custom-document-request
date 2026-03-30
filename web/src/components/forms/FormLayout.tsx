import * as React from "react";
import { cn } from "@/lib/utils";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

interface FormLayoutProps {
  title: string;
  subtitle?: string;
  children: React.ReactNode;
  footer?: React.ReactNode;
  className?: string;
  size?: "sm" | "md" | "lg";
}

export const FormLayout = React.forwardRef<HTMLDivElement, FormLayoutProps>(
  ({ title, subtitle, children, footer, className, size = "md" }, ref) => {
    const sizeClasses = {
      sm: "max-w-md",
      md: "max-w-lg",
      lg: "max-w-xl",
    };

    return (
      <div
        ref={ref}
        className={cn(
          "flex items-center justify-center min-h-screen p-4",
          className,
        )}
      >
        <Card className={cn("w-full", sizeClasses[size])}>
          <CardHeader>
            <CardTitle className="text-2xl">{title}</CardTitle>
            {subtitle && <CardDescription>{subtitle}</CardDescription>}
          </CardHeader>
          <CardContent>{children}</CardContent>
          {footer && <div className="px-6 pb-6">{footer}</div>}
        </Card>
      </div>
    );
  },
);

FormLayout.displayName = "FormLayout";
