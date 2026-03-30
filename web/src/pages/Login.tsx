import React from "react";
import { ShieldCheck, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { FormLayout } from "@/components/forms/FormLayout";
import { useNavigate } from "react-router-dom";
import { useKeycloak } from "@/contexts/KeycloakContext";

export default function Login() {
  const navigate = useNavigate();
  const { login, isLoading: keycloakLoading } = useKeycloak();

  const handleLogin = () => {
    login();
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-6 relative overflow-hidden bg-background">
      {/* Abstract Background Elements */}
      <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-primary/5 rounded-full blur-[120px] opacity-60" />
      <div className="absolute bottom-[-5%] right-[-5%] w-[30%] h-[30%] bg-primary/5 rounded-full blur-[100px]" />

      <main className="w-full max-w-[440px] z-10">
        <div className="text-center mb-10">
          <div className="inline-flex items-center justify-center mb-4">
            <div className="w-14 h-14 bg-primary rounded-2xl flex items-center justify-center shadow-xl shadow-primary/20">
              <ShieldCheck className="text-white w-8 h-8" />
            </div>
          </div>
          <h1 className="text-3xl font-bold mb-1">Document Request</h1>
          <p className="text-sm text-muted-foreground font-semibold tracking-[0.15em]">
            Enterprise IP Management
          </p>
        </div>

        <FormLayout
          title="Bem-vindo"
          subtitle="Acesse sua conta para gerenciar documentos"
        >
          <Button
            onClick={handleLogin}
            className="w-full py-4 gap-3"
            disabled={keycloakLoading}
          >
            {keycloakLoading ? (
              <>
                <Loader2 className="w-5 h-5 animate-spin" />
                <span>Redirecionando para Keycloak...</span>
              </>
            ) : (
              <>
                <ShieldCheck className="w-5 h-5 text-primary" />
                <span>Entrar com Keycloak SSO</span>
              </>
            )}
          </Button>
        </FormLayout>

        <footer className="mt-12 text-center space-y-4">
          <div className="flex justify-center gap-8">
            <button className="text-sm text-muted-foreground/60 hover:text-primary transition-colors">
              Suporte
            </button>
            <button className="text-sm text-muted-foreground/60 hover:text-primary transition-colors">
              Privacidade
            </button>
            <button className="text-sm text-muted-foreground/60 hover:text-primary transition-colors">
              Termos
            </button>
          </div>
          <p className="text-sm text-muted-foreground/40 lowercase">
            © 2024 Digital Archivist v2.4.0 • Enterprise IP Management
          </p>
        </footer>
      </main>
    </div>
  );
}
