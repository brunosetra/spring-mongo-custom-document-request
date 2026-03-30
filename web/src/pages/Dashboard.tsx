import React from "react";
import {
  FileText,
  Layers,
  Database,
  TrendingUp,
  Clock,
  User,
  ArrowRight,
  Search,
  Filter,
  Plus,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { StatCard } from "@/components/data/StatCard";
import { ActivityItem } from "@/components/data/ActivityItem";
import { DataTable } from "@/components/data/DataTable";
import { SearchInput } from "@/components/forms/SearchInput";
import { Breadcrumb } from "@/components/layout/Breadcrumb";
import { cn } from "@/lib/utils";
import { STATUS_COLORS, STATUS_LABELS } from "@/lib/constants";

export default function Dashboard() {
  const stats = [
    {
      title: "Total de Documentos",
      value: "12,482",
      trend: { value: "+12%", type: "up", label: "este mês" },
      icon: <FileText className="h-8 w-8" />,
    },
    {
      title: "Templates Ativos",
      value: "48",
      trend: { value: "Sincronizados", type: "neutral" },
      icon: <Layers className="h-8 w-8" />,
    },
    {
      title: "Tabelas de Domínio",
      value: "156",
      trend: { value: "Em uso", type: "neutral" },
      icon: <Database className="h-8 w-8" />,
    },
  ];

  const recentActivities = [
    {
      document: "NDA_GlobalIP_v2.pdf",
      status: "pending" as const,
      user: "Ricardo M.",
      date: "Hoje, 14:20",
      avatar: "https://picsum.photos/seed/user1/40/40",
    },
    {
      document: "Patente_Design_04.docx",
      status: "completed" as const,
      user: "Julia S.",
      date: "Ontem, 09:15",
      avatar: "https://picsum.photos/seed/user2/40/40",
    },
    {
      document: "Contrato_Prestacao_09.pdf",
      status: "overdue" as const,
      user: "Ana Clara",
      date: "02 Out, 11:30",
      avatar: "https://picsum.photos/seed/user3/40/40",
    },
  ];

  const templates = [
    { name: "Contrato de Sigilo (NDA)", category: "Jurídico", version: "v2.4" },
    {
      name: "Procuração Patente",
      category: "Propriedade Intelectual",
      version: "v1.1",
    },
    { name: "Cessão de Direitos", category: "Comercial", version: "v3.0" },
  ];

  const activitiesColumns: Array<{
    key: "document" | "status" | "user" | "date" | "actions";
    label: string;
    render?: (value: any, row: any) => React.ReactNode;
  }> = [
    { key: "document", label: "Documento" },
    {
      key: "status",
      label: "Status",
      render: (value: string) => (
        <span
          className={cn(
            "px-2.5 py-0.5 rounded-full text-xs font-semibold",
            STATUS_COLORS[value as keyof typeof STATUS_COLORS] || "bg-gray-500",
          )}
        >
          {STATUS_LABELS[value as keyof typeof STATUS_LABELS] || value}
        </span>
      ),
    },
    { key: "user", label: "Solicitante" },
    { key: "date", label: "Data" },
    {
      key: "actions",
      label: "Ação",
      render: () => (
        <button className="text-primary hover:underline text-sm font-medium">
          Ver
        </button>
      ),
    },
  ];

  return (
    <div className="space-y-10 max-w-[1600px] mx-auto">
      <section>
        <Breadcrumb
          items={[
            { label: "Home", path: "/dashboard" },
            { label: "Painel de Controle" },
          ]}
        />
        <h2 className="text-3xl font-bold mt-2">Painel de Controle</h2>
        <p className="text-sm text-muted-foreground mt-1 opacity-60">
          Visão Geral de Propriedade Intelectual
        </p>
      </section>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {stats.map((stat, index) => (
          <StatCard key={index} {...stat} />
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        <section className="lg:col-span-4 space-y-6">
          <div className="flex items-center justify-between px-2">
            <h3 className="text-lg font-semibold">Templates Disponíveis</h3>
            <Button
              variant="outline"
              size="sm"
              className="text-xs font-bold uppercase tracking-widest"
            >
              Ver Todos
            </Button>
          </div>
          <div className="space-y-3">
            {templates.map((template) => (
              <div
                key={template.name}
                className="bg-card p-5 rounded-xl flex items-center justify-between group hover:bg-accent hover:shadow-lg transition-all border border-border/5"
              >
                <div className="flex items-center gap-4">
                  <div className="w-10 h-10 rounded-xl bg-accent flex items-center justify-center text-primary">
                    <FileText className="w-5 h-5" />
                  </div>
                  <div>
                    <p className="text-sm font-bold">{template.name}</p>
                    <p className="text-xs text-muted-foreground opacity-50">
                      {template.category} • {template.version}
                    </p>
                  </div>
                </div>
                <button className="w-8 h-8 rounded-full bg-primary/5 text-primary flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                  <Plus className="w-5 h-5" />
                </button>
              </div>
            ))}
          </div>
        </section>

        <section className="lg:col-span-8 bg-card rounded-2xl overflow-hidden border border-border/5">
          <div className="px-8 py-6 flex items-center justify-between border-b border-border/10">
            <h3 className="text-lg font-semibold">Atividades Recentes</h3>
            <div className="flex items-center gap-4">
              <SearchInput placeholder="Filtrar pedidos..." className="w-64" />
              <button className="text-muted-foreground hover:text-primary transition-colors">
                <Filter className="w-5 h-5" />
              </button>
            </div>
          </div>

          <div className="overflow-x-auto">
            <DataTable data={recentActivities} columns={activitiesColumns} />
          </div>
        </section>
      </div>
    </div>
  );
}
