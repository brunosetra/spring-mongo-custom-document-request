import {
  Info,
  Settings,
  Plus,
  ChevronRight,
  Eye,
  Edit,
  History,
  Ban,
  CheckCircle2,
  Search,
  Filter,
  ChevronLeft,
  TrendingUp,
  Layers,
  FileText,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { StatCard } from "@/components/data/StatCard";
import { DataTable } from "@/components/data/DataTable";
import { SearchInput } from "@/components/forms/SearchInput";
import { Breadcrumb } from "@/components/layout/Breadcrumb";
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";

export default function Templates() {
  const stats = [
    {
      title: "Total Ativos",
      value: "124",
      trend: { value: "ativos", type: "neutral" },
      icon: <Layers className="h-8 w-8" />,
    },
    {
      title: "Em Revisão",
      value: "12",
      trend: { value: "em revisão", type: "neutral" },
      icon: <History className="h-8 w-8" />,
    },
    {
      title: "Arquivados",
      value: "08",
      trend: { value: "arquivados", type: "neutral" },
      icon: <Ban className="h-8 w-8" />,
    },
  ];

  const templates = [
    {
      id: "TMP-00124",
      name: "Contrato de Prestação de Serviços",
      desc: "Modelo padrão para novos consultores e parceiros externos.",
      version: "v2.4.1",
      status: "Active",
      icon: "article",
      color: "bg-blue-50 text-blue-600",
    },
    {
      id: "TMP-00215",
      name: "Política de Privacidade Global",
      desc: "Documento legal obrigatório para todos os portais regionais.",
      version: "v1.0.0",
      status: "Active",
      icon: "policy",
      color: "bg-purple-50 text-purple-600",
    },
    {
      id: "TMP-00082",
      name: "Termo de Confidencialidade (NDA)",
      desc: "Minuta de confidencialidade para reuniões pré-projeto.",
      version: "v3.2.0",
      status: "Inactive",
      icon: "history_edu",
      color: "bg-orange-50 text-orange-600",
    },
    {
      id: "TMP-00344",
      name: "Autorização de Uso de Imagem",
      desc: "Autorização para campanhas de marketing e redes sociais.",
      version: "v1.1.2",
      status: "Active",
      icon: "verified_user",
      color: "bg-emerald-50 text-emerald-600",
    },
  ];

  const templatesColumns = [
    { key: "name", label: "Nome" },
    {
      key: "desc",
      label: "Descrição",
      render: (value: string) => (
        <span className="text-sm text-muted-foreground max-w-xs truncate">
          {value}
        </span>
      ),
    },
    {
      key: "version",
      label: "Versão",
      render: (value: string) => (
        <span className="px-2 py-1 bg-accent text-muted-foreground text-[10px] font-bold rounded-md">
          {value}
        </span>
      ),
    },
    {
      key: "status",
      label: "Status",
      render: (value: string) => (
        <Badge
          variant={
            value === "Active"
              ? "default"
              : value === "Inactive"
                ? "secondary"
                : "destructive"
          }
        >
          {value}
        </Badge>
      ),
    },
    {
      key: "actions",
      label: "Ações",
      render: () => (
        <div className="flex items-center justify-end gap-2">
          <button
            className="p-2 hover:bg-accent rounded-lg transition-colors"
            title="Ver"
          >
            <Eye className="w-4 h-4" />
          </button>
          <button
            className="p-2 hover:bg-accent rounded-lg transition-colors"
            title="Editar"
          >
            <Edit className="w-4 h-4" />
          </button>
          <button
            className="p-2 hover:bg-accent rounded-lg transition-colors"
            title="Histórico"
          >
            <History className="w-4 h-4" />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-10 max-w-[1600px] mx-auto">
      <div className="flex justify-between items-end">
        <div>
          <Breadcrumb
            items={[
              { label: "Home", path: "/dashboard" },
              { label: "Templates" },
            ]}
          />
          <h2 className="text-3xl font-bold mt-2">Templates</h2>
          <p className="text-sm text-muted-foreground mt-1 opacity-60">
            Document Templates Management System
          </p>
        </div>
        <Button className="gap-2">
          <Plus className="w-5 h-5" />
          <span>Novo Template</span>
        </Button>
      </div>

      <div className="grid grid-cols-12 gap-6">
        <div className="col-span-8 bg-card rounded-2xl p-8 border border-border/5">
          <div className="flex justify-between items-start mb-8">
            <h3 className="text-lg font-semibold">Visão Geral dos Modelos</h3>
            <TrendingUp className="w-5 h-5 text-primary" />
          </div>
          <div className="grid grid-cols-3 gap-12">
            {stats.map((stat, index) => (
              <StatCard key={index} {...stat} />
            ))}
          </div>
        </div>
        <div className="col-span-4 bg-primary text-white rounded-2xl p-8 relative overflow-hidden">
          <div className="relative z-10">
            <h3 className="text-lg font-semibold mb-2">Otimização de Fluxo</h3>
            <p className="text-sm opacity-80 mb-6">
              Seus templates estão sendo utilizados em 85% dos novos processos
              automatizados.
            </p>
            <button className="text-sm bg-white/20 hover:bg-white/30 px-4 py-2 rounded-full transition-colors">
              Ver Insights
            </button>
          </div>
          <div className="absolute -right-8 -bottom-8 opacity-10">
            <Layers className="w-32 h-32" />
          </div>
        </div>
      </div>

      <div className="bg-card rounded-2xl border border-border/5 overflow-hidden">
        <div className="px-8 py-6 border-b border-border/5 flex justify-between items-center">
          <div className="relative w-96">
            <SearchInput
              placeholder="Pesquisar templates por nome ou tag..."
              className="w-full"
            />
          </div>
          <Button variant="outline" size="sm" className="p-2.5">
            <Filter className="w-5 h-5" />
          </Button>
        </div>

        <DataTable data={templates} columns={templatesColumns} />
      </div>
    </div>
  );
}
