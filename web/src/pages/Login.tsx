import React, { useState } from "react";
import { Mail, Lock, ArrowRight, ShieldCheck } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { FormLayout } from "@/components/forms/FormLayout";
import { FormField } from "@/components/forms/FormField";
import { useNavigate } from "react-router-dom";
import { useDebounce } from "@/hooks/useDebounce";
import { useLocalStorage } from "@/hooks/useLocalStorage";
import { toast } from "sonner";

export default function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [errors, setErrors] = useState<{ email?: string; password?: string }>(
    {},
  );

  const debouncedEmail = useDebounce(email, 300);
  const debouncedPassword = useDebounce(password, 300);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setErrors({});

    // Simple validation
    const newErrors: { email?: string; password?: string } = {};
    if (!email) {
      newErrors.email = "Email é obrigatório";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      newErrors.email = "Email inválido";
    }
    if (!password) {
      newErrors.password = "Senha é obrigatória";
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      setIsLoading(false);
      return;
    }

    try {
      // Simulate API call
      await new Promise((resolve) => setTimeout(resolve, 1000));
      toast.success("Login realizado com sucesso!");
      navigate("/dashboard");
    } catch (error) {
      toast.error("Erro ao fazer login. Tente novamente.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleForgotPassword = () => {
    toast.info("Link de recuperação de senha enviado para seu email");
  };

  const handleRequestAccount = () => {
    toast.info(
      "Solicitação de conta enviada. Entre em contato com o administrador.",
    );
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
          footer={
            <p className="text-center text-sm text-muted-foreground">
              Não tem acesso?{" "}
              <button
                onClick={handleRequestAccount}
                className="text-primary font-bold hover:underline"
              >
                Solicitar conta
              </button>
            </p>
          }
        >
          <form onSubmit={handleLogin} className="space-y-6">
            <FormField
              label="Email"
              error={errors.email}
              help="Digite seu email corporativo"
              required
            >
              <Input
                type="email"
                placeholder="nome@empresa.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                disabled={isLoading}
              />
            </FormField>

            <FormField
              label="Senha"
              error={errors.password}
              help="Digite sua senha de acesso"
              required
            >
              <div className="space-y-2">
                <div className="flex justify-between items-center">
                  <Label htmlFor="password" className="text-sm font-medium">
                    Senha
                  </Label>
                  <button
                    type="button"
                    onClick={handleForgotPassword}
                    className="text-sm text-primary font-bold hover:underline"
                  >
                    Esqueceu a senha?
                  </button>
                </div>
                <Input
                  id="password"
                  type="password"
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  disabled={isLoading}
                />
              </div>
            </FormField>

            <Button
              type="submit"
              className="w-full py-4 gap-2"
              disabled={isLoading}
            >
              {isLoading ? (
                <>
                  <span>Entrando...</span>
                </>
              ) : (
                <>
                  <span>Entrar</span>
                  <ArrowRight className="w-5 h-5" />
                </>
              )}
            </Button>
          </form>

          <div className="relative my-10">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t" />
            </div>
            <div className="relative flex justify-center">
              <span className="px-4 bg-background text-sm text-muted-foreground/60">
                Ou continue com
              </span>
            </div>
          </div>

          <Button
            variant="outline"
            className="w-full py-4 gap-3"
            disabled={isLoading}
          >
            <ShieldCheck className="w-5 h-5 text-primary" />
            <span>Keycloak SSO</span>
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
