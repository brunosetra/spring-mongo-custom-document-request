import * as React from "react";
import { Link, LinkProps } from "react-router-dom";
import { cn } from "@/lib/utils";

interface NavLinkProps extends LinkProps {
  active?: boolean;
  className?: string;
  children: React.ReactNode;
}

export const NavLink = React.forwardRef<HTMLAnchorElement, NavLinkProps>(
  ({ active = false, className, children, ...props }, ref) => {
    return (
      <Link
        ref={ref}
        className={cn(
          "inline-flex items-center gap-2 rounded-lg px-3 py-2 text-sm font-medium transition-colors",
          active
            ? "bg-primary text-primary-foreground"
            : "text-muted-foreground hover:bg-accent hover:text-accent-foreground",
          className,
        )}
        {...props}
      >
        {children}
      </Link>
    );
  },
);

NavLink.displayName = "NavLink";
