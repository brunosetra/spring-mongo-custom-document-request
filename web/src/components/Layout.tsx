import { LayoutDashboard, FileText, Layers, Database, Search, Plus, HelpCircle, Settings, Bell, LogOut, ChevronRight } from 'lucide-react';
import { NavLink, Link } from 'react-router-dom';
import { cn } from '../lib/utils';
import { Button } from './Button';

export function Sidebar() {
  const navItems = [
    { icon: LayoutDashboard, label: 'Dashboard', to: '/dashboard' },
    { icon: FileText, label: 'Documentos', to: '/documents' },
    { icon: Layers, label: 'Templates', to: '/templates' },
    { icon: Database, label: 'Tabelas de Domínio', to: '/domain-tables' },
    { icon: Search, label: 'Consulta Avançada', to: '/search' },
  ];

  return (
    <aside className="fixed left-0 top-0 h-screen w-64 bg-surface-container-low flex flex-col py-8 border-none">
      <div className="px-8 mb-10">
        <h1 className="text-2xl font-black text-primary">Archivist</h1>
        <p className="label-sm text-on-surface-variant opacity-60">Enterprise DMS</p>
      </div>

      <nav className="flex-1 space-y-1 px-4">
        {navItems.map((item) => (
          <NavLink
            key={item.label}
            to={item.to}
            className={({ isActive }) =>
              cn(
                'flex items-center px-4 py-3 rounded-xl transition-all duration-200 group',
                isActive
                  ? 'bg-surface-container-highest text-primary font-semibold'
                  : 'text-on-surface-variant hover:bg-surface-container-high hover:text-on-surface'
              )
            }
          >
            <item.icon className="w-5 h-5 mr-3" />
            <span className="text-sm">{item.label}</span>
          </NavLink>
        ))}
      </nav>

      <div className="px-6 mb-8">
        <Button className="w-full rounded-xl py-4 flex items-center justify-center gap-2">
          <Plus className="w-5 h-5" />
          <span>Novo Pedido</span>
        </Button>
      </div>

      <div className="px-4 space-y-1 border-t border-outline-variant/10 pt-6">
        <Link to="/support" className="flex items-center px-4 py-2 text-on-surface-variant hover:text-on-surface transition-colors text-sm font-medium">
          <HelpCircle className="w-5 h-5 mr-3" />
          <span>Suporte</span>
        </Link>
        <Link to="/settings" className="flex items-center px-4 py-2 text-on-surface-variant hover:text-on-surface transition-colors text-sm font-medium">
          <Settings className="w-5 h-5 mr-3" />
          <span>Configurações</span>
        </Link>
      </div>
    </aside>
  );
}

export function TopBar() {
  return (
    <header className="fixed top-0 right-0 left-64 h-16 bg-surface/80 backdrop-blur-xl z-30 flex items-center justify-between px-8 border-none">
      <div className="flex items-center gap-6">
        <span className="text-xl font-black text-on-surface">Archivist Enterprise</span>
        <div className="h-4 w-px bg-outline-variant/20" />
        <nav className="flex items-center gap-3 text-sm font-medium text-on-surface-variant">
          <span className="opacity-50">Breadcrumbs</span>
          <ChevronRight className="w-4 h-4 opacity-30" />
          <span className="text-primary border-b-2 border-primary py-5">Tenant: Global IP</span>
        </nav>
      </div>

      <div className="flex items-center gap-6">
        <div className="flex items-center bg-surface-container-low px-4 py-2 rounded-full w-64">
          <Search className="w-4 h-4 text-on-surface-variant/40 mr-2" />
          <input
            type="text"
            placeholder="Procurar nos arquivos..."
            className="bg-transparent border-none focus:ring-0 text-sm w-full placeholder:text-on-surface-variant/40 outline-none"
          />
        </div>
        
        <div className="flex items-center gap-4">
          <button className="p-2 text-on-surface-variant hover:text-primary transition-colors">
            <Bell className="w-5 h-5" />
          </button>
          <button className="p-2 text-on-surface-variant hover:text-primary transition-colors">
            <HelpCircle className="w-5 h-5" />
          </button>
        </div>

        <div className="h-8 w-px bg-outline-variant/20 mx-2" />

        <div className="flex items-center gap-3 pl-2">
          <div className="text-right">
            <p className="text-xs font-bold text-on-surface">Alexandre Ramos</p>
            <p className="label-sm text-on-surface-variant opacity-60">Admin</p>
          </div>
          <img
            src="https://picsum.photos/seed/admin/100/100"
            alt="User"
            className="w-10 h-10 rounded-full ring-2 ring-white shadow-sm"
          />
          <button className="ml-2 text-on-surface-variant hover:text-error transition-colors">
            <LogOut className="w-5 h-5" />
          </button>
        </div>
      </div>
    </header>
  );
}
