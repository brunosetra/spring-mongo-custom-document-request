import * as React from "react";
import { ChevronRight } from "lucide-react";
import { cn } from "@/lib/utils";

interface BreadcrumbItem {
  label: string;
  path?: string;
  icon?: React.ReactNode;
}

interface BreadcrumbProps {
  items: BreadcrumbItem[];
  currentPath?: string;
  separator?: React.ReactNode;
  className?: string;
}

export const Breadcrumb = React.forwardRef<HTMLDivElement, BreadcrumbProps>(
  (
    {
      items,
      currentPath,
      separator = <ChevronRight className="h-4 w-4" />,
      className,
    },
    ref,
  ) => {
    return (
      <nav ref={ref} aria-label="Breadcrumb" className={cn("", className)}>
        <ol className="flex items-center gap-2 text-sm">
          {items.map((item, index) => (
            <li key={index} className="flex items-center gap-2">
              {index > 0 && (
                <span className="text-muted-foreground">{separator}</span>
              )}
              {item.path ? (
                <a
                  href={item.path}
                  className="text-muted-foreground hover:text-primary transition-colors"
                >
                  {item.icon && <span className="mr-1">{item.icon}</span>}
                  {item.label}
                </a>
              ) : (
                <span className="text-primary font-medium">
                  {item.icon && <span className="mr-1">{item.icon}</span>}
                  {item.label}
                </span>
              )}
            </li>
          ))}
        </ol>
      </nav>
    );
  },
);

Breadcrumb.displayName = "Breadcrumb";
