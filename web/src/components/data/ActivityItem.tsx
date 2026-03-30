import * as React from "react";
import { cn } from "@/lib/utils";
import { Card, CardContent } from "@/components/ui/card";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";

export interface ActivityItemProps {
  document: string;
  status: "pending" | "completed" | "overdue" | "failed";
  user: string;
  date: string;
  avatar?: string;
  actions?: React.ReactNode;
  className?: string;
}

export const ActivityItem = React.forwardRef<HTMLDivElement, ActivityItemProps>(
  ({ document, status, user, date, avatar, actions, className }, ref) => {
    const getStatusColor = () => {
      switch (status) {
        case "pending":
          return "bg-yellow-100 text-yellow-700";
        case "completed":
          return "bg-green-100 text-green-700";
        case "overdue":
          return "bg-red-100 text-red-700";
        case "failed":
          return "bg-red-100 text-red-700";
        default:
          return "bg-gray-100 text-gray-700";
      }
    };

    const getStatusLabel = () => {
      switch (status) {
        case "pending":
          return "Pendente";
        case "completed":
          return "Concluído";
        case "overdue":
          return "Atrasado";
        case "failed":
          return "Falhou";
        default:
          return status;
      }
    };

    const getInitials = (name: string) => {
      return name
        .split(" ")
        .map((n) => n[0])
        .join("")
        .toUpperCase()
        .slice(0, 2);
    };

    return (
      <Card
        ref={ref}
        className={cn("hover:shadow-md transition-shadow", className)}
      >
        <CardContent className="p-4">
          <div className="flex items-start gap-4">
            <Avatar className="h-10 w-10 shrink-0">
              {avatar ? (
                <AvatarFallback>{getInitials(user)}</AvatarFallback>
              ) : (
                <AvatarFallback className="bg-primary/10 text-primary">
                  {getInitials(user)}
                </AvatarFallback>
              )}
            </Avatar>
            <div className="flex-1 min-w-0">
              <div className="flex items-start justify-between gap-4">
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-foreground truncate">
                    {document}
                  </p>
                  <div className="flex items-center gap-3 mt-1">
                    <span className="text-xs text-muted-foreground">
                      {user}
                    </span>
                    <span className="text-xs text-muted-foreground">•</span>
                    <span className="text-xs text-muted-foreground">
                      {date}
                    </span>
                  </div>
                </div>
                <div className="flex items-center gap-2 shrink-0">
                  <span
                    className={cn(
                      "px-2 py-1 rounded-full text-xs font-medium",
                      getStatusColor(),
                    )}
                  >
                    {getStatusLabel()}
                  </span>
                  {actions && (
                    <div className="flex items-center gap-1">{actions}</div>
                  )}
                </div>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
    );
  },
);

ActivityItem.displayName = "ActivityItem";
