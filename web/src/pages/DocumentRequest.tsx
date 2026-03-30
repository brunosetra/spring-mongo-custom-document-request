import {
  User,
  Calendar,
  Mail,
  Phone,
  MapPin,
  CloudUpload,
  FileText,
  Send,
  X,
  Info,
  Gavel,
  HelpCircle,
  ExternalLink,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { FormField } from "@/components/forms/FormField";
import { FormLayout } from "@/components/forms/FormLayout";
import { Breadcrumb } from "@/components/layout/Breadcrumb";
import { cn } from "@/lib/utils";

export default function DocumentRequest() {
  return (
    <div className="space-y-10 max-w-6xl mx-auto">
      <section>
        <Breadcrumb
          items={[
            { label: "Home", path: "/dashboard" },
            { label: "Solicitação de Documento" },
          ]}
        />
        <div className="flex items-center gap-4 mt-2">
          <div className="w-14 h-14 bg-primary/10 rounded-2xl flex items-center justify-center text-primary">
            <FileText className="w-8 h-8" />
          </div>
          <div>
            <h1 className="text-2xl font-bold text-foreground">
              Solicitação de Documento Pessoal
            </h1>
            <p className="text-sm text-muted-foreground mt-1 opacity-60">
              Template de formulário padronizado para requisição de
              identificação de colaboradores.
            </p>
          </div>
        </div>
      </section>

      <div className="grid grid-cols-12 gap-8">
        <div className="col-span-12 lg:col-span-8 space-y-8">
          <section className="bg-card p-8 rounded-2xl border border-border/5 shadow-sm">
            <h2 className="text-lg font-semibold mb-6 flex items-center gap-3">
              <User className="w-5 h-5 text-primary" />
              <span>Dados do Requerente</span>
            </h2>

            <form className="space-y-6">
              <FormField label="Nome Completo" required>
                <Input placeholder="Ex: João da Silva" />
              </FormField>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <FormField label="CPF" required>
                  <Input placeholder="000.000.000-00" />
                </FormField>
                <FormField label="Data de Nascimento" required>
                  <div className="relative">
                    <input
                      type="date"
                      className="w-full bg-accent border-none rounded-xl px-4 py-3 text-sm outline-none focus:ring-2 focus:ring-primary/20"
                    />
                    <Calendar className="absolute right-4 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground/40 pointer-events-none" />
                  </div>
                </FormField>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <FormField label="E-mail Corporativo" required>
                  <Input type="email" placeholder="nome@empresa.com" />
                </FormField>
                <FormField label="Telefone de Contato" required>
                  <Input placeholder="(00) 00000-0000" />
                </FormField>
              </div>

              <FormField label="Endereço Residencial" required>
                <Input placeholder="Rua, Número, Complemento, Bairro, Cidade - UF" />
                <p className="text-xs text-muted-foreground/60 italic mt-2">
                  Campo obrigatório para envio de documentos físicos.
                </p>
              </FormField>
            </form>
          </section>

          <section className="bg-card p-8 rounded-2xl border border-border/5 shadow-sm">
            <h2 className="text-lg font-semibold mb-6 flex items-center gap-3">
              <CloudUpload className="w-5 h-5 text-primary" />
              <span>Anexos Requeridos</span>
            </h2>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="border-2 border-dashed border-border/20 rounded-2xl p-8 text-center group hover:bg-accent transition-all cursor-pointer">
                <div className="w-12 h-12 bg-accent rounded-xl flex items-center justify-center mx-auto mb-4 group-hover:scale-110 transition-transform">
                  <CloudUpload className="w-6 h-6 text-muted-foreground/40 group-hover:text-primary transition-colors" />
                </div>
                <p className="text-sm font-bold text-foreground">
                  Documento de Identidade (Frente)
                </p>
                <p className="text-xs text-muted-foreground/40 mt-1">
                  PNG, JPG ou PDF (Máx 5MB)
                </p>
              </div>

              <div className="bg-accent rounded-2xl p-5 flex items-center gap-4 relative group">
                <div className="w-16 h-16 rounded-xl overflow-hidden bg-white flex-shrink-0 shadow-sm">
                  <img
                    src="https://picsum.photos/seed/doc/200/200"
                    className="w-full h-full object-cover opacity-80"
                    alt=""
                  />
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-bold text-foreground truncate">
                    comprovante_residencia.pdf
                  </p>
                  <p className="text-xs text-muted-foreground/60 mt-0.5">
                    1.2 MB • Carregado
                  </p>
                  <div className="mt-3 w-full bg-accent h-1 rounded-full overflow-hidden">
                    <div className="bg-primary h-full w-full" />
                  </div>
                </div>
                <button className="absolute top-3 right-3 p-1.5 text-muted-foreground/40 hover:text-destructive transition-colors opacity-0 group-hover:opacity-100">
                  <X className="w-4 h-4" />
                </button>
              </div>
            </div>
          </section>
        </div>

        <div className="col-span-12 lg:col-span-4 space-y-8">
          <div className="bg-card p-8 rounded-2xl border border-border/5 shadow-sm">
            <h3 className="text-sm text-muted-foreground/60 mb-6">
              Ações do Processo
            </h3>
            <div className="space-y-4">
              <Button className="w-full py-5 rounded-xl gap-3 text-base">
                <Send className="w-5 h-5" />
                <span>Submeter Pedido</span>
              </Button>
              <Button
                variant="outline"
                className="w-full py-5 rounded-xl text-base"
              >
                Cancelar
              </Button>
            </div>
            <div className="mt-8 flex items-start gap-4 bg-accent/10 p-4 rounded-xl border border-accent/10">
              <Info className="w-5 h-5 text-muted-foreground flex-shrink-0 mt-0.5" />
              <p className="text-xs text-muted-foreground leading-relaxed">
                Ao submeter, você concorda com os termos de processamento de
                dados da política de privacidade empresarial.
              </p>
            </div>
          </div>

          <div className="bg-card p-8 rounded-2xl border border-border/5 shadow-sm relative overflow-hidden group">
            <div className="relative z-10">
              <h3 className="text-sm text-primary mb-6">
                Metadados do Template
              </h3>
              <div className="space-y-6">
                <div>
                  <span className="text-xs text-muted-foreground/40 block mb-1">
                    Código
                  </span>
                  <span className="text-sm font-mono font-bold">
                    TMP-DOC-0024
                  </span>
                </div>
                <div>
                  <span className="text-xs text-muted-foreground/40 block mb-1">
                    Prioridade
                  </span>
                  <div className="flex items-center gap-2">
                    <div className="w-2.5 h-2.5 rounded-full bg-muted" />
                    <span className="text-sm font-bold">Urgente</span>
                  </div>
                </div>
                <div>
                  <span className="text-xs text-muted-foreground/40 block mb-1">
                    SLA de Aprovação
                  </span>
                  <span className="text-sm font-bold">48 Horas Úteis</span>
                </div>
              </div>
            </div>
            <Gavel className="absolute -bottom-10 -right-10 w-48 h-48 text-white/40 group-hover:scale-110 transition-transform duration-700" />
          </div>

          <div className="bg-background p-8 rounded-2xl border border-border/5 shadow-sm">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-10 h-10 rounded-full bg-accent flex items-center justify-center text-primary">
                <HelpCircle className="w-6 h-6" />
              </div>
              <span className="text-base font-semibold">Precisa de ajuda?</span>
            </div>
            <p className="text-sm text-muted-foreground/60 mb-6">
              Caso tenha dúvidas sobre os campos, consulte o nosso guia de
              preenchimento ou fale com o suporte.
            </p>
            <button className="text-xs font-bold text-primary flex items-center gap-2 hover:underline">
              <span>Acessar Central de Ajuda</span>
              <ExternalLink className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
