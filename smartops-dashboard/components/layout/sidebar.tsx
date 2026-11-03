'use client';

import { usePathname } from 'next/navigation';
import Link from 'next/link';
import { BarChart3, Clock, Settings, Zap, AlertCircle, Brain, Cog } from 'lucide-react';

const mainNavItems = [
  { href: '/dashboard', icon: BarChart3, label: 'Dashboard', emoji: '📊' },

  { href: '/services', icon: Zap, label: 'Services', emoji: '🔧' },

];





export function Sidebar() {
  const pathname = usePathname();

  const isActive = (href: string) => pathname === href || pathname.startsWith(href + '/');

  const NavItem = ({ href, label, emoji }: { href: string; label: string; emoji: string }) => (
    <Link
      href={href}
      className={`flex items-center gap-2 px-2.5 py-1.75 rounded-lg text-xs font-medium transition-all whitespace-nowrap ${
        isActive(href)
          ? 'nav-item-active'
          : 'text-text2 hover:bg-white/5 hover:text-text1'
      }`}
    >
      <span className="text-xs">{emoji}</span>
      {label}
    </Link>
  );

  return (
    <aside className="fixed left-0 top-13 bottom-0 w-45 border-r overflow-y-auto flex flex-col gap-1 p-3" style={{
      backgroundColor: 'rgba(8, 13, 26, 0.9)',
      borderColor: 'var(--border)',
    }}>
      {/* Main Section */}
      <div className="space-y-1">
        <div className="text-xs uppercase font-semibold px-2.5 py-2 text-text3" style={{ letterSpacing: '0.8px' }}>
          Main
        </div>
        <nav className="space-y-0.5">
          {mainNavItems.map((item) => (
            <NavItem key={item.href} href={item.href} label={item.label} emoji={item.emoji} />
          ))}
        </nav>
      </div>

      
    </aside>
  );
}
