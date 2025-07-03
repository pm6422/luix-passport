"use client"; // 增加 "use client" 以支持 Recharts 和 Carousel

// 路径: app/page.tsx

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Carousel, CarouselContent, CarouselItem, CarouselNext, CarouselPrevious } from "@/components/ui/carousel";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { Input } from "@/components/ui/input";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  AlertTriangle,
  Car,
  CheckCircle,
  FileText,
  LifeBuoy,
  LineChart,
  Lock,
  LogOut,
  Search,
  Settings,
  Shield,
  Siren,
  User,
  Users,
} from "lucide-react";
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";


// --- Helper Components (可以移到单独的文件中) ---

// KPI 指标卡片
const KpiCard = ({ title, value, icon: Icon, description }: { title: string; value: string; icon: React.ElementType; description: string; }) => (
  <Card className="bg-slate-800/50 border-slate-700 backdrop-blur-sm">
    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
      <CardTitle className="text-sm font-medium text-slate-300">{title}</CardTitle>
      <Icon className="h-4 w-4 text-slate-400" />
    </CardHeader>
    <CardContent>
      <div className="text-2xl font-bold text-teal-400">{value}</div>
      <p className="text-xs text-slate-400">{description}</p>
    </CardContent>
  </Card>
);

// 列表项
const ListItem = ({ icon: Icon, title, description, action }: { icon: React.ElementType; title: string; description: string; action: React.ReactNode; }) => (
  <div className="flex items-center justify-between p-2 rounded-lg transition-colors hover:bg-slate-700/50">
    <div className="flex items-center space-x-4">
      <Icon className="h-6 w-6 text-teal-400" />
      <div>
        <p className="font-semibold text-slate-100">{title}</p>
        <p className="text-sm text-slate-400">{description}</p>
      </div>
    </div>
    {action}
  </div>
);

// 图表数据
const chartData = [
  { month: "一月", fixed: 12 },
  { month: "二月", fixed: 19 },
  { month: "三月", fixed: 15 },
  { month: "四月", fixed: 23 },
  { month: "五月", fixed: 18 },
  { month: "六月", fixed: 25 },
];

// --- Main Page Component ---

export default function SecurityPortalPage() {
  return (
    <div className="flex min-h-screen w-full flex-col bg-slate-900 text-slate-50">
      {/* 页面头部 */}
      <header className="sticky top-0 z-30 flex h-16 items-center gap-4 border-b border-slate-700 bg-gradient-to-r from-slate-900 to-slate-800 px-4 sm:px-6">
        <h1 className="text-2xl font-bold">信息安全门户</h1>
        <div className="relative ml-auto flex-1 md:grow-0">
          <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-slate-400" />
          <Input
            type="search"
            placeholder="搜索情报、文章、服务..."
            className="w-full rounded-lg bg-slate-800 pl-8 md:w-[200px] lg:w-[336px] border-slate-700 focus:border-violet-500"
          />
        </div>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button
              variant="ghost"
              size="icon"
              className="overflow-hidden rounded-full"
            >
              <Avatar>
                <AvatarImage src="https://github.com/shadcn.png" alt="User Avatar" />
                <AvatarFallback>U</AvatarFallback>
              </Avatar>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="bg-slate-800 border-slate-700 text-slate-50">
            <DropdownMenuLabel>我的账户</DropdownMenuLabel>
            <DropdownMenuSeparator className="bg-slate-700"/>
            <DropdownMenuItem className="focus:bg-slate-700"><User className="mr-2 h-4 w-4" />个人资料</DropdownMenuItem>
            <DropdownMenuItem className="focus:bg-slate-700"><Settings className="mr-2 h-4 w-4" />设置</DropdownMenuItem>
            <DropdownMenuSeparator className="bg-slate-700"/>
            <DropdownMenuItem className="focus:bg-slate-700"><LogOut className="mr-2 h-4 w-4" />退出登录</DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </header>

      <main className="flex flex-1 flex-col gap-6 p-4 sm:px-6 sm:py-6">
        {/* 核心指标 */}
        <div className="grid gap-4 md:grid-cols-2 md:gap-8 lg:grid-cols-4">
          <KpiCard title="受保护资产" value="1,250" icon={Shield} description="+20.1% from last month" />
          <KpiCard title="已修复漏洞" value="89" icon={CheckCircle} description="+15 since last week" />
          <KpiCard title="活跃告警" value="12" icon={Siren} description="3 critical alerts" />
          <KpiCard title="安全评分" value="92/100" icon={LineChart} description="Improved by 5 points" />
        </div>

        {/* VTA 认证请求入口 */}
        <Card className="bg-slate-800/50 border-slate-700 backdrop-blur-sm">
          <CardHeader className="flex flex-row items-center justify-between">
            <div className="space-y-1.5">
              <CardTitle className="text-slate-50">车辆 VTA 认证请求</CardTitle>
              <CardDescription className="text-slate-400">为您的车辆或组件申请 VTA 安全认证</CardDescription>
            </div>
            <Button size="lg" className="bg-gradient-to-r from-violet-600 to-purple-600 hover:from-violet-500 hover:to-purple-500 text-white font-bold shadow-lg">
              <Car className="mr-2 h-5 w-5" />
              发起新请求
            </Button>
          </CardHeader>
        </Card>

        {/* 主要内容区域 */}
        <div className="grid gap-6 md:gap-8 lg:grid-cols-2 xl:grid-cols-3">
          <div className="grid auto-rows-max items-start gap-6 md:gap-8 lg:col-span-2">
            {/* 安全情报 & 部门成果 */}
            <Tabs defaultValue="intelligence">
              <TabsList className="grid w-full grid-cols-2 bg-slate-800/80 border-slate-700">
                <TabsTrigger value="intelligence" className="data-[state=active]:bg-violet-600 data-[state=active]:text-slate-50 text-slate-300">
                  <Siren className="mr-2 h-4 w-4" />安全情报
                </TabsTrigger>
                <TabsTrigger value="achievements" className="data-[state=active]:bg-violet-600 data-[state=active]:text-slate-50 text-slate-300">
                  <Users className="mr-2 h-4 w-4" />部门成果
                </TabsTrigger>
              </TabsList>
              <TabsContent value="intelligence">
                <Card className="bg-slate-800/50 border-slate-700 backdrop-blur-sm">
                  <CardHeader>
                    <CardTitle className="text-slate-50">最新安全情报</CardTitle>
                    <CardDescription className="text-slate-400">
                      最新的全球和内部安全威胁情报。
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-2">
                    <ListItem
                      icon={AlertTriangle}
                      title="Log4j 远程代码执行漏洞 (CVE-2025-12345)"
                      description="高危 | 影响范围: 核心服务A, B"
                      action={<Button variant="secondary" size="sm" className="bg-slate-700 hover:bg-slate-600 text-slate-200">查看详情</Button>}
                    />
                    <ListItem
                      icon={AlertTriangle}
                      title="新的钓鱼邮件活动针对财务部门"
                      description="中危 | 已拦截 35 封邮件"
                      action={<Button variant="secondary" size="sm" className="bg-slate-700 hover:bg-slate-600 text-slate-200">查看详情</Button>}
                    />
                    <ListItem
                      icon={Lock}
                      title="Struts2 历史漏洞在野利用"
                      description="低危 | 建议升级依赖版本"
                      action={<Button variant="secondary" size="sm" className="bg-slate-700 hover:bg-slate-600 text-slate-200">查看详情</Button>}
                    />
                  </CardContent>
                </Card>
              </TabsContent>
              <TabsContent value="achievements">
                <Card className="bg-slate-800/50 border-slate-700 backdrop-blur-sm">
                  <CardHeader>
                    <CardTitle className="text-slate-50">部门成果</CardTitle>
                    <CardDescription className="text-slate-400">
                      过去六个月的漏洞修复趋势。
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    <div className="h-60 w-full">
                      <ResponsiveContainer width="100%" height="100%">
                        <BarChart data={chartData}>
                          <CartesianGrid strokeDasharray="3 3" stroke="#475569" />
                          <XAxis dataKey="month" tick={{ fill: '#94a3b8' }} />
                          <YAxis tick={{ fill: '#94a3b8' }} />
                          <Tooltip cursor={{fill: 'rgba(139, 92, 246, 0.2)'}} contentStyle={{backgroundColor: '#1e293b', border: '1px solid #475569'}}/>
                          <Bar dataKey="fixed" fill="#8b5cf6" name="已修复漏洞" />
                        </BarChart>
                      </ResponsiveContainer>
                    </div>
                  </CardContent>
                </Card>
              </TabsContent>
            </Tabs>
          </div>

          <div className="grid auto-rows-max items-start gap-6 md:gap-8">
            {/* 安全服务入口 */}
            <Card className="bg-slate-800/50 border-slate-700 backdrop-blur-sm">
              <CardHeader>
                <CardTitle className="text-slate-50">安全服务入口</CardTitle>
                <CardDescription className="text-slate-400">快速访问各项安全能力</CardDescription>
              </CardHeader>
              <CardContent className="grid grid-cols-2 gap-4">
                <Button variant="outline" className="border-slate-700 bg-slate-800/80 hover:bg-slate-700/80 hover:text-teal-400 flex h-auto flex-col items-start p-4 text-slate-200">
                  <Shield className="mb-2 h-6 w-6" />
                  <p className="font-semibold">漏洞扫描</p>
                  <p className="text-left text-sm text-slate-400">对应用或主机进行漏洞扫描</p>
                </Button>
                <Button variant="outline" className="border-slate-700 bg-slate-800/80 hover:bg-slate-700/80 hover:text-teal-400 flex h-auto flex-col items-start p-4 text-slate-200">
                  <Siren className="mb-2 h-6 w-6" />
                  <p className="font-semibold">应急响应</p>
                  <p className="text-left text-sm text-slate-400">上报安全事件并获得支持</p>
                </Button>
                <Button variant="outline" className="border-slate-700 bg-slate-800/80 hover:bg-slate-700/80 hover:text-teal-400 flex h-auto flex-col items-start p-4 text-slate-200">
                  <FileText className="mb-2 h-6 w-6" />
                  <p className="font-semibold">安全评审</p>
                  <p className="text-left text-sm text-slate-400">新功能上线前的安全检查</p>
                </Button>
                <Button variant="outline" className="border-slate-700 bg-slate-800/80 hover:bg-slate-700/80 hover:text-teal-400 flex h-auto flex-col items-start p-4 text-slate-200">
                  <LifeBuoy className="mb-2 h-6 w-6" />
                  <p className="font-semibold">安全咨询</p>
                  <p className="text-left text-sm text-slate-400">获取安全专家的帮助</p>
                </Button>
              </CardContent>
            </Card>

            {/* 安防专栏 */}
            <Card className="bg-slate-800/50 border-slate-700 backdrop-blur-sm">
              <CardHeader>
                <CardTitle className="text-slate-50">安防专栏</CardTitle>
                <CardDescription className="text-slate-400">阅读最新的安全文章和指南</CardDescription>
              </CardHeader>
              <CardContent>
                <Carousel className="w-full max-w-xs mx-auto">
                  <CarouselContent>
                    {[
                      { title: "如何防范社会工程学攻击", desc: "了解常见的攻击手段，保护您的敏感信息不被泄露。" },
                      { title: "企业数据安全最佳实践", desc: "从访问控制到加密，全面了解如何保护企业数据。" },
                      { title: "供应链安全：你需要知道的", desc: "第三方风险正在加剧，学习如何管理和缓解供应链风险。" }
                    ].map((item, index) => (
                      <CarouselItem key={index}>
                        <div className="p-1">
                          <Card className="bg-slate-800/80 border-slate-700">
                            <CardContent className="flex aspect-video items-center justify-center p-6 flex-col">
                              <h3 className="font-semibold text-lg mb-2 text-teal-400 text-center">{item.title}</h3>
                              <p className="text-sm text-slate-300 text-center">{item.desc}</p>
                              <Button variant="link" className="mt-4 text-violet-400 hover:text-violet-300">阅读全文</Button>
                            </CardContent>
                          </Card>
                        </div>
                      </CarouselItem>
                    ))}
                  </CarouselContent>
                  <CarouselPrevious className="border-slate-600 bg-slate-700 hover:bg-slate-600 text-slate-200" />
                  <CarouselNext className="border-slate-600 bg-slate-700 hover:bg-slate-600 text-slate-200" />
                </Carousel>
              </CardContent>
            </Card>
          </div>
        </div>
      </main>
    </div>
  );
}
