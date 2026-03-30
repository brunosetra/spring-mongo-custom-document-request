import {
  Info,
  Settings,
  Plus,
  ChevronRight,
  Trash2,
  HelpCircle,
  GripVertical,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { FormLayout } from "@/components/forms/FormLayout";
import { FormField } from "@/components/forms/FormField";
import { Breadcrumb } from "@/components/layout/Breadcrumb";
import { cn } from "@/lib/utils";

export default function TemplateEditor() {
  return (
    <div className="space-y-12 max-w-5xl mx-auto">
      <div className="flex items-end justify-between">
        <div>
          <Breadcrumb
            items={[
              { label: "Home", path: "/dashboard" },
              { label: "Templates", path: "/templates" },
              { label: "Novo Template" },
            ]}
          />
          <h2 className="text-xl font-semibold mt-2">Novo Template</h2>
          <p className="text-sm text-muted-foreground mt-1 opacity-60">
            Defina a estrutura de metadados para requisições de documentos.
          </p>
        </div>
        <div className="flex items-center gap-4">
          <Button variant="outline">Visualizar Preview</Button>
          <Button className="px-8">Salvar Template</Button>
        </div>
      </div>

      <div className="space-y-12">
        <section>
          <div className="flex items-center gap-3 mb-6">
            <div className="w-8 h-8 rounded-lg bg-primary/5 text-primary flex items-center justify-center">
              <Info className="w-5 h-5" />
            </div>
            <h3 className="text-lg font-semibold">Informações Gerais</h3>
          </div>
          <div className="bg-card rounded-2xl p-8 border border-border/5 shadow-sm">
            <div className="grid grid-cols-12 gap-8">
              <div className="col-span-8 space-y-6">
                <FormField label="Nome do Template" required>
                  <Input placeholder="Ex: Contratos de Fornecedores Externos" />
                </FormField>
                <FormField label="Descrição">
                  <textarea
                    className="w-full bg-accent border-none rounded-xl px-4 py-3 focus:ring-2 focus:ring-primary/20 transition-all text-sm resize-none h-32 outline-none placeholder:text-muted-foreground/40"
                    placeholder="Descreva o propósito deste template para os usuários..."
                  />
                </FormField>
              </div>
              <div className="col-span-4 space-y-6">
                <FormField label="Versão" required>
                  <Input
                    value="v1.0.0-draft"
                    readOnly
                    className="bg-accent/30 font-mono text-xs text-muted-foreground/60"
                  />
                </FormField>
                <FormField label="Status" required>
                  <div className="flex items-center justify-between bg-accent p-4 rounded-xl">
                    <span className="text-sm font-medium text-muted-foreground">
                      Template Ativo
                    </span>
                    <div className="w-12 h-6 bg-primary rounded-full relative">
                      <div className="absolute right-1 top-1 w-4 h-4 bg-white rounded-full" />
                    </div>
                  </div>
                </FormField>
              </div>
            </div>
          </div>
        </section>

        <section>
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <div className="w-8 h-8 rounded-lg bg-primary/5 text-primary flex items-center justify-center">
                <Settings className="w-5 h-5" />
              </div>
              <h3 className="text-lg font-semibold">Configuração de Campos</h3>
            </div>
            <span className="text-sm bg-accent px-3 py-1 rounded-md text-muted-foreground/60">
              2 campos configurados
            </span>
          </div>

          <div className="space-y-4">
            {[
              {
                name: "Razão Social",
                type: "String",
                entry: "Input",
                validation: "#this.length() > 5",
                min: 5,
                max: 255,
              },
              {
                name: "CNPJ",
                type: "Number",
                entry: "Input",
                validation: "#this.matches('^[0-9]{14}$')",
                format: "00.000.000/0000-00",
                min: 14,
                max: 14,
              },
            ].map((field, idx) => (
              <div
                key={idx}
                className="group relative bg-card border border-transparent hover:border-border/10 hover:shadow-lg hover:shadow-primary/5 rounded-2xl p-8 transition-all"
              >
                <div className="absolute -left-4 top-1/2 -translate-y-1/2 flex flex-col gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                  <button className="w-8 h-8 bg-background shadow-md border border-border/10 rounded-full flex items-center justify-center text-muted-foreground/40 hover:text-primary transition-colors">
                    <GripVertical className="w-4 h-4" />
                  </button>
                </div>

                <div className="grid grid-cols-12 gap-8">
                  <div className="col-span-4 space-y-4">
                    <FormField label="Nome do Campo" required>
                      <Input value={field.name} />
                    </FormField>
                    <div className="grid grid-cols-2 gap-4">
                      <FormField label="Tipo Dado">
                        <select className="w-full bg-accent border-none rounded-xl px-3 py-3 text-sm font-medium outline-none">
                          <option>{field.type}</option>
                        </select>
                      </FormField>
                      <FormField label="Entrada">
                        <select className="w-full bg-accent border-none rounded-xl px-3 py-3 text-sm font-medium outline-none">
                          <option>{field.entry}</option>
                        </select>
                      </FormField>
                    </div>
                  </div>

                  <div className="col-span-8 space-y-4">
                    <FormField label="Validação">
                      <Input
                        placeholder="#this.length() > 5"
                        value={field.validation}
                      />
                    </FormField>
                    <div className="grid grid-cols-2 gap-4">
                      <FormField label="Mínimo">
                        <Input type="number" value={field.min} />
                      </FormField>
                      <FormField label="Máximo">
                        <Input type="number" value={field.max} />
                      </FormField>
                    </div>
                    <FormField label="Formato">
                      <Input
                        placeholder="00.000.000/0000-00"
                        value={field.format}
                      />
                    </FormField>
                  </div>

                  <div className="col-span-12 flex justify-end gap-3 pt-4 border-t border-border/5">
                    <Button variant="outline" size="sm">
                      <Trash2 className="w-4 h-4 mr-2" />
                      Remover
                    </Button>
                    <Button variant="outline" size="sm">
                      <HelpCircle className="w-4 h-4 mr-2" />
                      Ajuda
                    </Button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
}
