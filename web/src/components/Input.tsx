import React from 'react';
import { cn } from '../lib/utils';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  icon?: React.ReactNode;
}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ className, label, error, icon, ...props }, ref) => {
    return (
      <div className="space-y-2 w-full">
        {label && <label className="block title-sm text-on-surface px-1">{label}</label>}
        <div className="relative group">
          {icon && (
            <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-on-surface-variant/50 group-focus-within:text-primary transition-colors">
              {icon}
            </div>
          )}
          <input
            ref={ref}
            className={cn(
              'w-full bg-surface-container-low border-none rounded-md px-4 py-3 focus:ring-2 focus:ring-primary/20 transition-all text-sm outline-none placeholder:text-on-surface-variant/40',
              icon && 'pl-11',
              error && 'ring-2 ring-error/20 bg-error-container/10',
              className
            )}
            {...props}
          />
        </div>
        {error && <p className="text-xs text-error font-medium px-1">{error}</p>}
      </div>
    );
  }
);
