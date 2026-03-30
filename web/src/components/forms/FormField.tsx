import * as React from "react";
import { cn } from "@/lib/utils";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";

interface FormFieldProps {
  label: string;
  error?: string;
  help?: string;
  required?: boolean;
  className?: string;
  children: React.ReactNode;
}

export const FormField = React.forwardRef<HTMLDivElement, FormFieldProps>(
  ({ label, error, help, required = false, className, children }, ref) => {
    return (
      <div ref={ref} className={cn("space-y-2 w-full", className)}>
        <Label
          htmlFor={label.toLowerCase().replace(/\s+/g, "-")}
          className={cn(required && "text-destructive")}
        >
          {label}
          {required && <span className="text-destructive ml-1">*</span>}
        </Label>
        {children}
        {error && (
          <p className="text-sm text-destructive font-medium">{error}</p>
        )}
        {help && !error && (
          <p className="text-sm text-muted-foreground">{help}</p>
        )}
      </div>
    );
  },
);

FormField.displayName = "FormField";
