// Application constants

export const APP_NAME = "DocRequest Service";
export const APP_VERSION = "1.0.0";

export const API_BASE_URL = "/api";

export const PAGINATION_DEFAULT_PAGE = 1;
export const PAGINATION_DEFAULT_PAGE_SIZE = 10;
export const PAGINATION_MAX_PAGE_SIZE = 100;

export const SEARCH_DEFAULT_DEBOUNCE_MS = 300;

export const TOAST_DEFAULT_DURATION = 4000;

export const FORM_DEFAULT_SIZE = "md";

export const THEME_STORAGE_KEY = "theme";

export const AUTH_TOKEN_KEY = "auth_token";
export const AUTH_USER_KEY = "auth_user";

export const STATUS_COLORS = {
  pending: "bg-yellow-500",
  completed: "bg-green-500",
  overdue: "bg-red-500",
  failed: "bg-red-600",
  active: "bg-blue-500",
  inactive: "bg-gray-500",
} as const;

export const STATUS_LABELS = {
  pending: "Pendente",
  completed: "Concluído",
  overdue: "Atrasado",
  failed: "Falhou",
  active: "Ativo",
  inactive: "Inativo",
} as const;

export const SORT_DIRECTIONS = {
  ASC: "asc",
  DESC: "desc",
} as const;

export const DATE_FORMATS = {
  DATE: "dd/MM/yyyy",
  DATETIME: "dd/MM/yyyy HH:mm",
  ISO: "yyyy-MM-dd",
  ISO_DATETIME: "yyyy-MM-dd HH:mm:ss",
} as const;
