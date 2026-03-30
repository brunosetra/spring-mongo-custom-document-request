import { Toaster as SonnerToaster } from "sonner";

export function Toaster() {
  return (
    <SonnerToaster
      position="bottom-right"
      richColors
      closeButton
      toastOptions={{
        classNames: {
          toast: "min-w-[350px] rounded-lg border bg-background p-4 shadow-lg",
          title: "font-semibold",
          description: "text-sm opacity-90",
        },
      }}
    />
  );
}
