import * as React from "react";
import { Search } from "lucide-react";
import { cn } from "@/lib/utils";
import { Input } from "@/components/ui/input";

interface SearchInputProps {
  placeholder?: string;
  value?: string;
  onSearch?: (query: string) => void;
  onChange?: (value: string) => void;
  debounceMs?: number;
  className?: string;
  disabled?: boolean;
}

export const SearchInput = React.forwardRef<HTMLInputElement, SearchInputProps>(
  (
    {
      placeholder = "Procurar...",
      value,
      onSearch,
      onChange,
      debounceMs = 300,
      className,
      disabled = false,
    },
    ref,
  ) => {
    const [debouncedValue, setDebouncedValue] = React.useState<string>(
      value || "",
    );
    const [isDebouncing, setIsDebouncing] = React.useState(false);

    React.useEffect(() => {
      if (value !== undefined) {
        setDebouncedValue(value);
      }
    }, [value]);

    React.useEffect(() => {
      if (isDebouncing) {
        const timer = setTimeout(() => {
          setDebouncedValue(value || "");
          setIsDebouncing(false);
          if (onSearch) {
            onSearch(debouncedValue);
          }
        }, debounceMs);

        return () => clearTimeout(timer);
      }
    }, [debounceMs, isDebouncing, onSearch, debouncedValue, value]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      const newValue = e.target.value;
      setDebouncedValue(newValue);
      setIsDebouncing(true);
      if (onChange) {
        onChange(newValue);
      }
    };

    return (
      <div className={cn("relative", className)}>
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
        <Input
          ref={ref}
          type="text"
          placeholder={placeholder}
          value={debouncedValue}
          onChange={handleChange}
          disabled={disabled}
          className="pl-10"
        />
      </div>
    );
  },
);

SearchInput.displayName = "SearchInput";
