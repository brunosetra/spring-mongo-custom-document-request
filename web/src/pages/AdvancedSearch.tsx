import {
  Search,
  Filter,
  Calendar,
  User,
  FileText,
  ChevronRight,
  Download,
  Share2,
  MoreVertical,
  History,
  Bookmark,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { SearchInput } from "@/components/forms/SearchInput";
import { FormField } from "@/components/forms/FormField";
import { Breadcrumb } from "@/components/layout/Breadcrumb";
import { cn } from "@/lib/utils";

export default function AdvancedSearch() {
  const searchShortcuts = [
    { label: "Meus Documentos", count: 12 },
    { label: "Pendentes de Assinatura", count: 5 },
    { label: "Atualizados Recentemente", count: 28 },
    { label: "Templates Jurídicos", count: 14 },
  ];

  const results = [
    {
      id: "DOC-9921",
      name: "Contrato_Social_GlobalIP_2024.pdf",
      type: "PDF",
      size: "2.4 MB",
      date: "22 Mar, 2024",
      status: "Finalizado",
    },
    {
      id: "DOC-8812",
      name: "Relatorio_Financeiro_Q1.xlsx",
      type: "XLSX",
      size: "1.1 MB",
      date: "15 Mar, 2024",
      status: "Em Revisão",
    },
    {
      id: "DOC-7734",
      name: "NDA_Parceiro_Logistica.pdf",
      type: "PDF",
      size: "840 KB",
      date: "10 Mar, 2024",
      status: "Finalizado",
    },
    {
      id: "DOC-6651",
      name: "Manual_Treinamento_v2.docx",
      type: "DOCX",
      size: "4.5 MB",
      date: "05 Mar, 2024",
      status: "Rascunho",
    },
  ];

  return (
    <div className="space-y-10 max-w-7xl mx-auto">
      <section>
        <Breadcrumb
          items={[
            { label: "Home", path: "/dashboard" },
            { label: "Consulta Avançada" },
          ]}
        />
        <h2 className="text-3xl font-bold mt-2">Consulta Avançada</h2>
        <p className="text-sm text-muted-foreground mt-1 opacity-60">
          Localize documentos em todo o ecossistema Archivist
        </p>
      </section>

      <div className="grid grid-cols-12 gap-8">
        <aside className="col-span-12 lg:col-span-4 space-y-8">
          <div className="bg-card rounded-2xl p-8 border border-border/5 shadow-sm">
            <h3 className="text-base font-semibold mb-6 flex items-center gap-2">
              <Filter className="w-5 h-5 text-primary" />
              <span>Filtros de Busca</span>
            </h3>

            <div className="space-y-6">
              <FormField label="Palavra-chave" required>
                <Input placeholder="Ex: Nome do contrato, ID..." />
              </FormField>

              <FormField label="Tipo de Documento">
                <select className="w-full bg-accent border-none rounded-xl px-4 py-3 text-sm font-medium outline-none">
                  <option>Todos os tipos</option>
                  <option>PDF</option>
                  <option>DOCX</option>
                  <option>XLSX</option>
                </select>
              </FormField>

              <div className="grid grid-cols-2 gap-4">
                <FormField label="Data Início">
                  <input
                    type="date"
                    className="w-full bg-accent border-none rounded-xl px-3 py-3 text-xs outline-none"
                  />
                </FormField>
                <FormField label="Data Fim">
                  <input
                    type="date"
                    className="w-full bg-accent border-none rounded-xl px-3 py-3 text-xs outline-none"
                  />
                </FormField>
              </div>

              <FormField label="Solicitante" required>
                <Input placeholder="Nome do usuário" />
              </FormField>

              <div className="pt-4 space-y-3">
                <Button className="w-full py-4 rounded-xl">
                  Aplicar Filtros
                </Button>
                <button className="w-full text-sm text-muted-foreground/40 hover:text-primary transition-colors">
                  Limpar todos os filtros
                </button>
              </div>
            </div>
          </div>

          <div className="space-y-4">
            <h4 className="text-sm text-muted-foreground/40 px-4">
              Atalhos de Busca
            </h4>
            <div className="grid grid-cols-1 gap-2">
              {searchShortcuts.map((shortcut) => (
                <button
                  key={shortcut.label}
                  className="flex items-center justify-between px-6 py-4 bg-background hover:bg-accent rounded-xl border border-border/5 transition-all group"
                >
                  <span className="text-sm font-medium text-foreground group-hover:text-primary">
                    {shortcut.label}
                  </span>
                  <span className="text-xs bg-accent px-2 py-0.5 rounded-md text-muted-foreground/40">
                    {shortcut.count}
                  </span>
                </button>
              ))}
            </div>
          </div>
        </aside>

        <main className="col-span-12 lg:col-span-8 space-y-6">
          <div className="flex items-center justify-between px-4">
            <p className="text-sm text-muted-foreground/40">
              Foram encontrados{" "}
              <span className="text-foreground font-bold">156 documentos</span>
            </p>
            <div className="flex items-center gap-2">
              <span className="text-sm text-muted-foreground/40">
                Ordenar por:
              </span>
              <select className="bg-transparent border-none text-xs font-bold text-primary outline-none cursor-pointer">
                <option>Mais Recentes</option>
                <option>Nome (A-Z)</option>
                <option>Tamanho</option>
              </select>
            </div>
          </div>

          <div className="space-y-4">
            {results.map((doc) => (
              <div
                key={doc.id}
                className="bg-card p-6 rounded-2xl border border-border/5 shadow-sm group hover:shadow-lg hover:shadow-primary/5 transition-all flex items-center gap-6"
              >
                <div className="w-14 h-14 bg-accent rounded-2xl flex items-center justify-center text-muted-foreground/40 group-hover:bg-primary/5 group-hover:text-primary transition-all">
                  <FileText className="w-7 h-7" />
                </div>

                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-3 mb-1">
                    <h4 className="text-base font-bold text-foreground truncate">
                      {doc.name}
                    </h4>
                    <Badge variant="outline">{doc.type}</Badge>
                  </div>
                  <div className="flex items-center gap-4 text-xs text-muted-foreground/40 font-medium">
                    <span>ID: {doc.id}</span>
                    <span className="w-1 h-1 rounded-full bg-border/40" />
                    <span>{doc.size}</span>
                    <span className="w-1 h-1 rounded-full bg-border/40" />
                    <span>Modificado em {doc.date}</span>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <span
                    className={cn(
                      "px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider",
                      doc.status === "Finalizado"
                        ? "bg-primary/10 text-primary"
                        : "bg-secondary/10 text-secondary",
                    )}
                  >
                    {doc.status}
                  </span>
                  <div className="flex items-center gap-1 ml-4">
                    <button className="p-2 text-muted-foreground/20 hover:text-primary transition-colors">
                      <Download className="w-4 h-4" />
                    </button>
                    <button className="p-2 text-muted-foreground/20 hover:text-primary transition-colors">
                      <Share2 className="w-4 h-4" />
                    </button>
                    <button className="p-2 text-muted-foreground/20 hover:text-primary transition-colors">
                      <Bookmark className="w-4 h-4" />
                    </button>
                    <button className="p-2 text-muted-foreground/20 hover:text-primary transition-colors">
                      <MoreVertical className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>

          <div className="pt-6 flex justify-center">
            <Button variant="outline" className="gap-2 rounded-xl px-10">
              <span>Carregar mais resultados</span>
              <History className="w-4 h-4" />
            </Button>
          </div>
        </main>
      </div>
    </div>
  );
}
