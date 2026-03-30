// Shared TypeScript types

export interface PaginationParams {
  page: number;
  pageSize: number;
}

export interface PaginationResponse<T> {
  data: T[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export interface SortParams {
  field: string;
  direction: "asc" | "desc";
}

export interface FilterParams {
  field: string;
  operator: string;
  value: any;
}

export interface QueryParams {
  filters?: FilterParams[];
  sort?: SortParams;
  pagination?: PaginationParams;
}

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: string;
  message?: string;
}

export interface ApiError {
  message: string;
  code?: string;
  details?: any;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

export interface Document {
  id: string;
  name: string;
  type: string;
  size: number;
  createdAt: string;
  updatedAt: string;
  status: string;
}

export interface Template {
  id: string;
  name: string;
  description?: string;
  category: string;
  version: string;
  createdAt: string;
  updatedAt: string;
}

export interface DomainTable {
  id: string;
  name: string;
  description?: string;
  fields: DomainTableField[];
  createdAt: string;
  updatedAt: string;
}

export interface DomainTableField {
  id: string;
  name: string;
  type: string;
  required: boolean;
  defaultValue?: any;
}

export interface User {
  id: string;
  name: string;
  email: string;
  avatar?: string;
  role: string;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface SearchQuery {
  query: string;
  filters?: any[];
  sort?: any;
  page?: number;
  pageSize?: number;
}
