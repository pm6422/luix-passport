// 路径: app/page.tsx

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { Input } from "@/components/ui/input";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  AlertTriangle,
  CheckCircle,
  ChevronRight,
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

// --- Helper Components (可以移到单独的文件中) ---

// KPI 指标卡片
const KpiCard = ({ title, value, icon: Icon, description }: { title: string; value: string; icon: React.ElementType; description: string; }) => (
  <Card>
    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
      <CardTitle className="text-sm font-medium">{title}</CardTitle>
      <Icon className="h-4 w-4 text-muted-foreground" />
    </CardHeader>
    <CardContent>
      <div className="text-2xl font-bold">{value}</div>
      <p className="text-xs text-muted-foreground">{description}</p>
    </CardContent>
  </Card>
);

// 列表项
const ListItem = ({ icon: Icon, title, description, action }: { icon: React.ElementType; title: string; description: string; action: React.ReactNode; }) => (
  <div className="flex items-center justify-between">
    <div className="flex items-center space-x-4">
      <Icon className="h-6 w-6 text-muted-foreground" />
      <div>
        <p className="font-semibold">{title}</p>
        <p className="text-sm text-muted-foreground">{description}</p>
      </div>
    </div>
    {action}
  </div>
);

// --- Main Page Component ---

export default function SecurityPortalPage() {
  return (
    <div className="flex min-h-screen w-full flex-col bg-muted/40">
      {/* 页面头部 */}
      <header className="sticky top-0 z-30 flex h-14 items-center gap-4 border-b bg-background px-4 sm:static sm:h-auto sm:border-0 sm:bg-transparent sm:px-6 py-4">
        <h1 className="text-2xl font-bold">信息安全门户</h1>
        <div className="relative ml-auto flex-1 md:grow-0">
          <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
          <Input
            type="search"
            placeholder="搜索情报、文章、服务..."
            className="w-full rounded-lg bg-background pl-8 md:w-[200px] lg:w-[336px]"
          />
        </div>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button
              variant="outline"
              size="icon"
              className="overflow-hidden rounded-full"
            >
              <Avatar>
                <AvatarImage src="https://github.com/shadcn.png" alt="User Avatar" />
                <AvatarFallback>U</AvatarFallback>
              </Avatar>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuLabel>我的账户</DropdownMenuLabel>
            <DropdownMenuSeparator />
            <DropdownMenuItem><User className="mr-2 h-4 w-4" />个人资料</DropdownMenuItem>
            <DropdownMenuItem><Settings className="mr-2 h-4 w-4" />设置</DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem><LogOut className="mr-2 h-4 w-4" />退出登录</DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </header>

      <main className="flex flex-1 flex-col gap-4 p-4 sm:px-6 sm:py-0 md:gap-8">
        {/* 核心指标 */}
        <div className="grid gap-4 md:grid-cols-2 md:gap-8 lg:grid-cols-4">
          <KpiCard title="受保护资产" value="1,250" icon={Shield} description="+20.1% from last month" />
          <KpiCard title="已修复漏洞" value="89" icon={CheckCircle} description="+15 since last week" />
          <KpiCard title="活跃告警" value="12" icon={Siren} description="3 critical alerts" />
          <KpiCard title="安全评分" value="92/100" icon={LineChart} description="Improved by 5 points" />
        </div>

        {/* 主要内容区域 */}
        <div className="grid gap-4 md:gap-8 lg:grid-cols-2 xl:grid-cols-3">
          <div className="grid auto-rows-max items-start gap-4 md:gap-8 lg:col-span-2">
            {/* 安全情报 & 部门成果 */}
            <Tabs defaultValue="intelligence">
              <TabsList className="grid w-full grid-cols-2">
                <TabsTrigger value="intelligence">
                  <Siren className="mr-2 h-4 w-4" />安全情报
                </TabsTrigger>
                <TabsTrigger value="achievements">
                  <Users className="mr-2 h-4 w-4" />部门成果
                </TabsTrigger>
              </TabsList>
              <TabsContent value="intelligence">
                <Card>
                  <CardHeader>
                    <CardTitle>最新安全情报</CardTitle>
                    <CardDescription>
                      最新的全球和内部安全威胁情报。
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <ListItem
                      icon={AlertTriangle}
                      title="Log4j 远程代码执行漏洞 (CVE-2025-12345)"
                      description="高危 | 影响范围: 核心服务A, B"
                      action={<Button variant="secondary" size="sm">查看详情</Button>}
                    />
                    <ListItem
                      icon={AlertTriangle}
                      title="新的钓鱼邮件活动针对财务部门"
                      description="中危 | 已拦截 35 封邮件"
                      action={<Button variant="secondary" size="sm">查看详情</Button>}
                    />
                    <ListItem
                      icon={Lock}
                      title="Struts2 历史漏洞在野利用"
                      description="低危 | 建议升级依赖版本"
                      action={<Button variant="secondary" size="sm">查看详情</Button>}
                    />
                  </CardContent>
                </Card>
              </TabsContent>
              <TabsContent value="achievements">
                <Card>
                  <CardHeader>
                    <CardTitle>部门成果</CardTitle>
                    <CardDescription>
                      本季度安全部门的主要工作成果和贡献。
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    {/* 图表占位符 */}
                    <div className="my-4 h-60 w-full rounded-lg border border-dashed flex items-center justify-center">
                      <p className="text-muted-foreground">月度漏洞修复趋势图 (Chart Placeholder)</p>
                    </div>
                    <ListItem
                      icon={CheckCircle}
                      title="完成核心系统渗透测试"
                      description="发现并修复 5 个高危漏洞"
                      action={<span className="text-sm text-muted-foreground">2周前</span>}
                    />
                    <ListItem
                      icon={CheckCircle}
                      title="组织全员安全意识培训"
                      description="参与率 98%，平均得分 91"
                      action={<span className="text-sm text-muted-foreground">1个月前</span>}
                    />
                  </CardContent>
                </Card>
              </TabsContent>
            </Tabs>
          </div>

          <div className="grid auto-rows-max items-start gap-4 md:gap-8">
            {/* 安全服务入口 */}
            <Card>
              <CardHeader>
                <CardTitle>安全服务入口</CardTitle>
                <CardDescription>快速访问各项安全能力</CardDescription>
              </CardHeader>
              <CardContent className="grid grid-cols-2 gap-4">
                <Button variant="outline" className="flex h-auto flex-col items-start p-4">
                  <Shield className="mb-2 h-6 w-6" />
                  <p className="font-semibold">漏洞扫描</p>
                  <p className="text-left text-sm text-muted-foreground">对应用或主机进行漏洞扫描</p>
                </Button>
                <Button variant="outline" className="flex h-auto flex-col items-start p-4">
                  <Siren className="mb-2 h-6 w-6" />
                  <p className="font-semibold">应急响应</p>
                  <p className="text-left text-sm text-muted-foreground">上报安全事件并获得支持</p>
                </Button>
                <Button variant="outline" className="flex h-auto flex-col items-start p-4">
                  <FileText className="mb-2 h-6 w-6" />
                  <p className="font-semibold">安全评审</p>
                  <p className="text-left text-sm text-muted-foreground">新功能上线前的安全检查</p>
                </Button>
                <Button variant="outline" className="flex h-auto flex-col items-start p-4">
                  <LifeBuoy className="mb-2 h-6 w-6" />
                  <p className="font-semibold">安全咨询</p>
                  <p className="text-left text-sm text-muted-foreground">获取安全专家的帮助</p>
                </Button>
              </CardContent>
            </Card>

            {/* 安防专栏 */}
            <Card>
              <CardHeader>
                <CardTitle>安防专栏</CardTitle>
                <CardDescription>阅读最新的安全文章和指南</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <a href="#" className="flex items-center justify-between hover:bg-muted/50 p-2 rounded-lg transition-colors">
                  <p>如何防范社会工程学攻击</p>
                  <ChevronRight className="h-4 w-4 text-muted-foreground" />
                </a>
                <a href="#" className="flex items-center justify-between hover:bg-muted/50 p-2 rounded-lg transition-colors">
                  <p>企业数据安全保护最佳实践</p>
                  <ChevronRight className="h-4 w-4 text-muted-foreground" />
                </a>
                <a href="#" className="flex items-center justify-between hover:bg-muted/50 p-2 rounded-lg transition-colors">
                  <p>供应链安全：你需要知道的</p>
                  <ChevronRight className="h-4 w-4 text-muted-foreground" />
                </a>
              </CardContent>
            </Card>
          </div>
        </div>
      </main>
    </div>
  );
}
