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
    <main className="min-h-screen flex flex-col items-center justify-center p-6 bg-background">
      <div className="text-center mb-10">
        <div className="inline-flex items-center justify-center mb-4">
          <div className="w-14 h-14 bg-primary rounded-2xl flex items-center justify-center shadow-xl shadow-primary/20">
            <ShieldCheck className="text-white w-8 h-8" />
          </div>
        </div>
        <h1 className="text-3xl font-bold mb-1">Document Request</h1>
        <p className="text-sm text-muted-foreground font-semibold tracking-[0.15em]">
          Custom Document Management System
        </p>
      </div>

      <FormLayout
        title="Welcome"
        subtitle="Sign in to your account to manage documents"
        className="flex-1"
      >
        <Button
          onClick={handleLogin}
          className="w-full py-4 gap-3"
          disabled={keycloakLoading}
        >
          {keycloakLoading ? (
            <>
              <Loader2 className="w-5 h-5 animate-spin" />
              <span>Redirect to Keycloak...</span>
            </>
          ) : (
            <>
              <ShieldCheck className="w-5 h-5 text-white" />
              <span>Sign in with Keycloak SSO</span>
            </>
          )}
        </Button>
      </FormLayout>

      <footer className="mt-12 text-center space-y-4">
        <div className="flex justify-center gap-8">
          <button className="text-sm text-muted-foreground/60 hover:text-primary transition-colors">
            Support
          </button>
          <button className="text-sm text-muted-foreground/60 hover:text-primary transition-colors">
            Privacy
          </button>
          <button className="text-sm text-muted-foreground/60 hover:text-primary transition-colors">
            Terms
          </button>
        </div>
        <p className="text-sm text-muted-foreground/40 lowercase">
          © 2026 Document Request v0.0.1 • Developed by Bruno Setra
        </p>
      </footer>
    </main>
  );
}
