import React from 'react';
import { cn } from '../lib/utils';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'tertiary' | 'ghost';
  size?: 'sm' | 'md' | 'lg';
}

export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  ({ className, variant = 'primary', size = 'md', ...props }, ref) => {
    const baseStyles = 'inline-flex items-center justify-center font-semibold transition-all active:scale-[0.98] disabled:opacity-50 disabled:pointer-events-none';
    
    const variants = {
      primary: 'primary-gradient text-white shadow-lg shadow-primary/20 rounded-md',
      secondary: 'bg-transparent border border-outline-variant/15 text-on-surface hover:bg-surface-container-low rounded-md',
      tertiary: 'text-primary hover:underline p-0',
      ghost: 'hover:bg-surface-container-low text-on-surface-variant rounded-md',
    };

    const sizes = {
      sm: 'px-3 py-1.5 text-xs',
      md: 'px-6 py-3 text-sm',
      lg: 'px-8 py-4 text-base',
    };

    return (
      <button
        ref={ref}
        className={cn(baseStyles, variants[variant], variant !== 'tertiary' && sizes[size], className)}
        {...props}
      />
    );
  }
);
